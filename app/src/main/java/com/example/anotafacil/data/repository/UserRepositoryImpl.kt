package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.entity.toDomain
import com.example.anotafacil.data.firestore.FirestoreSchema
import com.example.anotafacil.data.firestore.model.FirestoreUser
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.domain.Response.HomeResponse
import com.example.anotafacil.domain.Response.SellerConnectionState
import com.example.anotafacil.domain.datastore.UserDataStore
import com.example.anotafacil.domain.model.SellerHomeUsers
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.WhichScreenToGo
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ownerCodeRepositoryImpl: OwnerCodeRepositoryImpl,
    private val userDao: UserDao,
    private val userDataStore: UserDataStore,
) : UserRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    private val currentUserUid: Flow<String?> = callbackFlow {

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }

        auth.addAuthStateListener(listener)

        awaitClose { auth.removeAuthStateListener(listener) }

    }.distinctUntilChanged()


    init { observeRemoteUser().launchIn(repositoryScope) }


    @OptIn(ExperimentalCoroutinesApi::class)
    private val observeUserDao: Flow<Result<User?>> =
        currentUserUid.flatMapLatest { uid ->
            if (uid == null) return@flatMapLatest flowOf(Result.success(null))

            userDao.observeUserDao(uid)
                .filterNotNull()
                .map { user ->
                    Result.success(
                        user.let {
                            User(
                                uid = it.uid,
                                name = it.name,
                                email = it.email,
                                ownerId = it.ownerId,
                                role = it.role
                            )
                        }
                    )
                }
        }.stateIn(
            repositoryScope,
            SharingStarted.WhileSubscribed(),
            Result.success(null)
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeRemoteUser(): Flow<Result<User>> {
        return currentUserUid.flatMapLatest { uid ->

            if (uid == null) {
                return@flatMapLatest flowOf(
                    Result.failure(Exception("Usuário não autenticado"))
                )
            }

            firestore
                .collection(FirestoreSchema.Collections.USERS)
                .document(uid)
                .snapshots(MetadataChanges.INCLUDE)
                .filter { snapshot ->
                    !snapshot.metadata.isFromCache &&
                            !snapshot.metadata.hasPendingWrites()
                }
                .map { snapshot ->

                    if (!snapshot.exists()) {
                        return@map Result.failure(Exception("Usuário remoto não encontrado"))
                    }

                    val remoteUser = snapshot.toObject(FirestoreUser::class.java)
                        ?: throw IllegalStateException("Erro ao recuperar os dados do usuário")

                    val localUser = userDao.getUserDao(uid)
                        ?: return@map Result.failure<User>(
                            Exception("Usuário local não encontrado")
                        )

                    if (remoteUser.ownerId != localUser.ownerId) {
                        userDataStore.updateSellerLinkState(
                            uid = uid,
                            ownerId = remoteUser.ownerId
                        )
                    }

                    userDao.updateRemoteUserInfo(
                        uid = uid,
                        name = remoteUser.name,
                        email = remoteUser.email,
                        ownerId = remoteUser.ownerId
                    )

                    Result.success(
                        User(
                            uid = uid,
                            name = remoteUser.name,
                            email = remoteUser.email,
                            ownerId = remoteUser.ownerId,
                            role = localUser.role
                        )
                    )
                }
                .catch { error ->
                    if (error is CancellationException) throw error

                    emit(Result.failure(error))
                }
        }
    }


    override suspend fun observeOwnerInfo(ownerUid: String?): Flow<HomeResponse> {

        val firebaseUserUid = auth.currentUser?.uid
            ?: return flowOf(HomeResponse.NotAuthenticated)

        return firestore
            .collection(FirestoreSchema.Collections.USERS)
            .document(ownerUid ?: firebaseUserUid)
            .snapshots()
            .map { documentSnapshot ->
                if (!documentSnapshot.exists()) return@map HomeResponse.NotFound("Nenhum dado encontrado")

                val firestoreUser = documentSnapshot.toObject(FirestoreUser::class.java)
                    ?: return@map HomeResponse.ErrorToParse

                val owner = User(
                    uid = documentSnapshot.id,
                    name = firestoreUser.name,
                    email = firestoreUser.email,
                    ownerId = firestoreUser.ownerId,
                    role = UserRole.OWNER
                )


                if (owner.uid != firebaseUserUid) saveUserLocally(user = owner)

                HomeResponse.Success(
                    SellerHomeUsers(owner = owner)
                )
            }
            .catch { e ->
                if (e is CancellationException) throw e

                emit(HomeResponse.Error(e.message))
            }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSellerConnectionWithOwner(): Flow<SellerConnectionState> {
        return currentUserUid.flatMapLatest { uid ->

            if (uid == null) {
                return@flatMapLatest flowOf(
                    SellerConnectionState.NotConnected
                )
            }

            combine(
                userDao.observeUserDao(uid).filterNotNull(),
                userDataStore.observeSellerDisconnectionPending(uid)
            ) { user, pendingDisconnection ->

                when {
                    user.role == UserRole.OWNER ->
                        SellerConnectionState.NotApplicable

                    pendingDisconnection ->
                        SellerConnectionState.Disconnected

                    !user.ownerId.isNullOrBlank() ->
                        SellerConnectionState.Connected

                    else ->
                        SellerConnectionState.NotConnected
                }
            }
        }.distinctUntilChanged()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun loadSellerAndOwnerInfo(): Flow<HomeResponse> {
        return observeUserDao.flatMapLatest { result ->
            if (result.isFailure) {
                return@flatMapLatest flowOf(
                    HomeResponse.Error(result.exceptionOrNull()?.message)
                )
            }


            val user = result.getOrNull() ?: return@flatMapLatest emptyFlow()


            val ownerId = user.ownerId ?: return@flatMapLatest flowOf(HomeResponse.NotConnected)


            observeOwnerInfo(ownerId).map { ownerResult ->
                when (ownerResult) {
                    is HomeResponse.Success -> {
                        val owner = ownerResult.users.owner

                        HomeResponse.Success(
                            SellerHomeUsers(
                                seller = user,
                                owner = owner
                            )
                        )
                    }

                    is HomeResponse.NotFound -> HomeResponse.NotFound(ownerResult.message)

                    is HomeResponse.Error -> HomeResponse.Error(ownerResult.message)

                    else -> ownerResult
                }
            }
        }.catch { error ->
            if (error is CancellationException) throw error

            emit(HomeResponse.Error(error.message))
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
            Log.d("UserRepository", "getCurrentUser: $uid")
            val user = userDao.getUserDao(uid ?: firebaseUserUid)
                ?: return Result.failure(Exception("Usuário não encontrado no banco local"))
            Log.d("UserRepository", "getCurrentUser: $user")
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<Result<User>> {
        return observeUserDao.map { resultSeller ->
            if (resultSeller.isFailure) return@map Result.failure(resultSeller.exceptionOrNull()!!)

            val seller = resultSeller.getOrNull()
                ?: return@map Result.failure(Exception("Erro ao obter informações do vendedor"))


            Result.success(seller)
        }
    }


    override suspend fun becomeOwner(): Result<Boolean> {

        val uid = auth.currentUser?.uid
            ?: return Result.failure(
                Exception("Usuário não autenticado")
            )

        return try {
            userDao.updateRole(
                uid = uid,
                userRole = UserRole.OWNER
            )

            Result.success(true)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Log.e("UserRepository", "Error becoming owner", e)
            Result.failure(e)
        }
    }


    override suspend fun becomeSeller(): Result<Boolean> {

        val uid = auth.currentUser?.uid
            ?: return Result.failure(
                Exception("Usuário não autenticado")
            )

        return try {
            userDao.updateRole(
                uid = uid,
                userRole = UserRole.SELLER
            )

            Result.success(true)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Log.e("UserRepository", "Error becoming seller", e)
            Result.failure(e)
        }
    }


    override suspend fun saveChanges(
        userWithNewName: User?,
        userWithNewEmail: User?,
    ): Result<String?> {
        var messageRequestEmailSent: String? = null

        if (userWithNewName != null) {
            saveNewName(userWithNewName)
                .onSuccess {
                    messageRequestEmailSent = "Nome alterado com sucesso"
                }
                .onFailure {
                    return Result.failure(it)
                }
        }
        if (userWithNewEmail != null) {
            requestEmailChange(userWithNewEmail.email)
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
            firestore
                .collection(FirestoreSchema.Collections.USERS)
                .document(user.uid)
                .update(FirestoreSchema.CommonUserFields.NAME, user.name)
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



        firestore
            .collection(FirestoreSchema.Collections.USERS)
            .document(user.uid)
            .update(FirestoreSchema.CommonUserFields.EMAIL, user.email)
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

            Result.success("Uma messagem de verificação foi enviado para o seu e-mail.")
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun checkEmailChange(): Result<Boolean> {
        return try {

            val pendingEmail = userDataStore
                .pendingEmailChange
                .first()
                ?: return Result.success(false)


            val firebaseUser = auth.currentUser ?: return Result.success(false)

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


    override suspend fun verifyingAndDeletingOwnerFromSeller(): Result<Boolean> {
        val firebaseUserUid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Usuário não está logado"))

        val ownerId = userDao.getUserDao(firebaseUserUid)?.ownerId
            ?: return Result.failure(
                Exception("Vendedor não está vinculado")
            )

        if (appDatabase.hasPendingData(ownerId))
            return Result.failure(Exception("Há dados pendentes. Salve antes de desconectar-se!"))


        return ownerCodeRepositoryImpl.disconnectOwnerFromSeller(firebaseUserUid)
    }



    override suspend fun clearData(): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(
                    Exception("Usuário não está logado")
                )

            appDatabase.clearSellerData().getOrThrow()

            userDao.updateOwnerId(uid = uid, ownerId = null)

            userDataStore.clearSellerLinkState(uid)

            Result.success(true)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun resolveInitialDestination(): Result<WhichScreenToGo> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.success(WhichScreenToGo.LOGIN)

            val ownerDocument = firestore
                .collection(FirestoreSchema.Collections.SUBSCRIPTION_ENTITLEMENTS)
                .document(uid)
                .get(Source.SERVER)
                .await()

            if (
                ownerDocument.exists()
                && ownerDocument.getBoolean(
                    FirestoreSchema.SubscriptionFields.HAS_OWNER_ACCESS
                ) == true
            ) {
                return Result.success(WhichScreenToGo.OWNER_HOME)
            }

            val sellerDocument = firestore
                .collection(FirestoreSchema.Collections.USERS)
                .document(uid)
                .get(Source.SERVER)
                .await()

            if (!sellerDocument.exists()) {
                return Result.failure(
                    IllegalStateException("Conta autenticada sem documento de usuário.")
                )
            }

            val ownerId = sellerDocument.getString(FirestoreSchema.UsersFields.OWNER_ID)

            Result.success(
                if (ownerId.isNullOrBlank()) {
                    WhichScreenToGo.ROLE_SECTION
                } else {
                    WhichScreenToGo.SELLER_HOME
                }
            )
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                return Result.success(WhichScreenToGo.ERROR)
            }

            Result.failure(e)
        } catch (e: CancellationException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acknowledgeSellerDisconnection(uid: String) {
        userDataStore.clearSellerDisconnectionPending(uid)
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
}