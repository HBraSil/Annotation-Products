package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.entity.toDomain
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.SellerHomeUsers
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
) : UserRepository {


    override suspend fun getOwner(): Flow<HomeResult> {

        val firebaseUserUid = auth.currentUser?.uid
            ?: return flowOf(HomeResult.Disconnected)

        return try {

            firestore
                .collection("owners")
                .document(firebaseUserUid)
                .snapshots()
                .map { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        return@map HomeResult.NotFound
                    }

                    val owner = documentSnapshot.toObject(User::class.java)
                        ?: return@map HomeResult.ErrorToParse

                    saveUserLocally(
                        uid = firebaseUserUid,
                        user = owner
                    )

                    HomeResult.Success(
                        SellerHomeUsers(owner = owner)
                    )
                }

        } catch (e: Exception) {

            Log.d(
                "UserRepository",
                "Erro ao buscar owner.",
                e
            )
            flowOf(HomeResult.Error(e.message))
        }
    }


    private fun getSeller(): Flow<User?> {

        val firebaseUserUid = auth.currentUser?.uid ?: return flowOf(null)

        return firestore
            .collection("sellers")
            .document(firebaseUserUid)
            .snapshots()
            .map { documentSnapshot ->
                if (!documentSnapshot.exists()) return@map (null)

                val user = documentSnapshot.toObject(User::class.java) ?: return@map (null)

                Log.d("UserRepositoryGetSeller", "nome do Seller: ${user.name}")
                if (user.ownerId != null) {
                    saveUserLocally(
                        uid = firebaseUserUid,
                        user = user
                    )
                }

                user
            }
            .catch {
            }
    }


    private suspend fun getOwnerById(ownerId: String): Result<User> {
        return try {

            val document = firestore
                .collection("owners")
                .document(ownerId)
                .get()
                .await()

            if (!document.exists()) {
                return Result.failure(Exception("Proprietário não encontrado"))
            }

            val user = document.toObject(User::class.java)
                ?: return Result.failure(Exception("Erro ao converter usuário"))

            saveUserLocally(
                uid = ownerId,
                user = user
            )

            Result.success(user)
        } catch (e: Exception) {

            Log.d(
                "UserRepository",
                "Erro ao buscar owner.",
                e
            )

            Result.failure(e)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSellerData(): Flow<HomeResult> {

        return getSeller()
            .mapLatest { seller ->
                if (seller == null) return@mapLatest HomeResult.NotFound

                val ownerId = seller.ownerId ?: return@mapLatest HomeResult.Disconnected

                val owner = getOwnerById(ownerId)
                    .getOrElse {
                        return@mapLatest HomeResult.Error(it.message)
                    }

                Log.d("UserRepository", "SellerNameGetSellerData: ${seller.name}")

                HomeResult.Success(
                    SellerHomeUsers(
                        seller = seller,
                        owner = owner
                    )
                )
            }
            .catch {
                emit(
                    HomeResult.Error(message = it.message)
                )
            }

    }


    private suspend fun saveUserLocally(
        uid: String,
        user: User,
    ) {
        userDao.saveUserDao(
            UserEntity(
                uid = uid,
                name = user.name,
                email = user.email,
                ownerId = user.ownerId,
                role = user.role
            )
        )
    }


    override suspend fun getCurrentUser(uid: String?): Result<User> {
        return try {

            val firebaseUserUid =
                auth.currentUser?.uid ?: return Result.failure(Exception("Usuário não está logado"))

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
            ?: return Result.failure(
                Exception("Usuário não autenticado")
            )

        return try {

            val owner = User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                role = UserRole.OWNER
            )

            firestore
                .collection("owners")
                .document(firebaseUser.uid)
                .set(owner)
                .await()

            firestore
                .collection("sellers")
                .document(firebaseUser.uid)
                .delete()
                .await()

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
}