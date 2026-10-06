package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.entity.toDomain
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.domain.datastore.UserDataStore
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.SellerHomeUsers
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val userDataStore: UserDataStore
) : UserRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    private val currentUserUid: Flow<String?> = callbackFlow {

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }

        auth.addAuthStateListener(listener)

        awaitClose { auth.removeAuthStateListener(listener) }

    }.distinctUntilChanged()


    override suspend fun verifyingIfSellerCanDisconnect(): Result<Boolean> {
        val firebaseUserUid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Usuário não está logado"))

        userDao.updateOwnerId(uid = firebaseUserUid, ownerId = null)
        return appDatabase.clearSellerData()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private val sellerFlow: SharedFlow<User?> =
        currentUserUid.flatMapLatest { uid ->
            if (uid == null) flowOf(null)
            else getSeller(uid)
        }
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5_000),
                replay = 1
            )



    override suspend fun getOwner(ownerUid: String?): Flow<HomeResult> {

        val firebaseUserUid = auth.currentUser?.uid
            ?: return flowOf(HomeResult.Disconnected)

        return try {

            firestore
                .collection("owners")
                .document(ownerUid ?: firebaseUserUid)
                .snapshots()
                .map { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        return@map HomeResult.NotFound
                    }

                    val owner = documentSnapshot.toObject(User::class.java)
                        ?: return@map HomeResult.ErrorToParse

                    saveUserLocally(user = owner)

                    HomeResult.Success(
                        SellerHomeUsers(owner = owner)
                    )
                }

        } catch (e: Exception) {
            flowOf(HomeResult.Error(e.message))
        }
    }


    private fun getSeller(uid: String): Flow<User?> {
        return firestore
            .collection("sellers")
            .document(uid)
            .snapshots()
            .map { documentSnapshot ->
                if (!documentSnapshot.exists()) return@map null

                val user = documentSnapshot.toObject(User::class.java) ?: return@map null

                if (user.ownerId != null) saveUserLocally(user = user)

                user
            }
    }


    override fun observeSellerConnection(): Flow<HomeResult>  {
        return sellerFlow.map { seller ->
            if (seller == null) return@map HomeResult.NotFound

            if (seller.ownerId != null) return@map HomeResult.Connected

            val localUser = userDao.getUserDao(seller.uid)


            if (localUser?.ownerId != null) HomeResult.Disconnected
            else HomeResult.NotConnected
        }
            .catch { throwable ->
                emit(HomeResult.Error(throwable.message))
            }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSellerData(): Flow<HomeResult> {
        return try {
            sellerFlow.flatMapLatest { seller ->

                val seller = seller ?: return@flatMapLatest flowOf(HomeResult.NotFound)

                val ownerId = seller.ownerId ?: return@flatMapLatest flowOf(HomeResult.Disconnected)


                getOwner(ownerId).map { ownerResult ->
                    when (ownerResult) {
                        is HomeResult.Success -> {
                            val owner = ownerResult.users.owner
                            HomeResult.Success(
                                SellerHomeUsers(
                                    seller = seller,
                                    owner = owner
                                )
                            )
                        }

                        is HomeResult.Error -> HomeResult.Error(ownerResult.message)

                        else -> HomeResult.Error("Erro ao buscar proprietário")
                    }
                }
            }
        } catch (throwable: Throwable) {
            flowOf(HomeResult.Error(message = throwable.message))
        }
    }


    private suspend fun saveUserLocally(user: User) {
        userDao.saveUserDao(
            UserEntity(
                uid = user.uid,
                name = user.name,
                email = user.email,
                ownerId = user.ownerId,
                role = user.role
            )
        )
    }


    override suspend fun getCurrentUser(uid: String?): Result<User> {
        return try {
            val firebaseUserUid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuário não está logado"))

            val user = userDao.getUserDao(uid ?: firebaseUserUid)
                ?: return Result.failure(Exception("Usuário não encontrado no banco local"))

            Log.d("UserRepository", "User from local database: $user")
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun becomeOwner(): Result<Boolean> {

        val firebaseUser = auth.currentUser
            ?: return Result.failure(Exception("Usuário não autenticado"))

        return try {
            val documentReference = firestore.collection("sellers")
                .document(firebaseUser.uid)

            val seller = documentReference.get().await().toObject(User::class.java)
                ?: return Result.failure(Exception("Erro ao converter usuário"))

            Log.d("UserRepository", "Seller: $seller")

            val owner = seller.copy(
                uid = firebaseUser.uid,
                ownerId = null,
                role = UserRole.OWNER
            )

            firestore
                .collection("owners")
                .document(firebaseUser.uid)
                .set(owner)
                .await()

            documentReference.delete().await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun signOut(): Result<Boolean> {
        return try {
            if (auth.currentUser == null) Result.failure<Exception>(Exception("Usuário não está logado"))

            auth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao sair da conta", e)
            Result.failure(e)
        }
    }


    override suspend fun saveChanges(newUserName: User?, newUserEmail: User?): Result<String?> {
        var messageRequestEmailSent: String? = null

        if (newUserName != null) saveNewName(newUserName)
        if (newUserEmail != null) {
            requestEmailChange(newUserEmail.email)
                .onSuccess {
                    messageRequestEmailSent = it
                }
                .onFailure {
                    return Result.failure(it)
                }
        }

        return Result.success(messageRequestEmailSent)
    }


    private suspend fun saveNewName(user: User): Result<Boolean> {
        return try {
            val userRole = when (user.role) {
                UserRole.OWNER -> "owners"
                UserRole.SELLER -> "sellers"
            }

            firestore
                .collection(userRole)
                .document(user.uid)
                .update("name", user.name)
                .await()

            userDao.updateUserName(uid = user.uid, name = user.name)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private suspend fun saveNewEmail(user: User): Result<Boolean> {
        Log.d("UserRepository", "Saving new email for user: $user")
        userDao.updateEmail(uid = user.uid, email = user.email)

        val userRole = when (user.role) {
            UserRole.OWNER -> "owners"
            UserRole.SELLER -> "sellers"
        }

        firestore
            .collection(userRole)
            .document(user.uid)
            .update("email", user.email)
            .await()

        return Result.success(true)
    }


    private suspend fun requestEmailChange(newEmail: String): Result<String> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.failure(Exception("Usuário não autenticado"))

            firebaseUser
                .verifyBeforeUpdateEmail(newEmail)
                .await()

            userDataStore.savePendingEmail(newEmail)

            Result.success("Um e-mail de verificação foi enviado para o seu e-mail.")
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun checkEmailChange(): Result<Boolean> {
        return try {

            val pendingEmail = userDataStore
                .pendingEmailChange
                .first()
                ?: return Result.success(false)


            val firebaseUser = auth.currentUser
                ?: return Result.success(false)

            if (firebaseUser.email == pendingEmail) return Result.success(false)

            val user = userDao.getUserDao(firebaseUser.uid)
                ?.toDomain()
                ?.copy(email = pendingEmail)
                ?: return Result.success(false)


            saveNewEmail(user)

            firebaseUser.reload().await()

            Result.success(false)

        } catch (e: FirebaseAuthInvalidUserException) {
            Log.d("UserRepositoryCheck", "checkEmailChange: ${e.message}")
            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}