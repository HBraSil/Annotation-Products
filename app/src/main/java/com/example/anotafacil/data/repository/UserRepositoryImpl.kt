package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
): UserRepository {

    private val firebaseUserUid = auth.currentUser?.uid

    override suspend fun getSellerUser(): Result<User?> {

        val firebaseUser = auth.currentUser
            ?: return Result.success(null)

        return try {

            val document = firestore
                .collection("sellers")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!document.exists()) {
                return Result.success(null)
            }

            val user = document.toObject(User::class.java)
                ?: return Result.success(null)

            Log.d(
                "UserRepository",
                "Seller: $user"
            )

            saveUserLocally(
                uid = firebaseUser.uid,
                user = user
            )

            Result.success(user)
        } catch (e: Exception) {

            Log.d(
                "UserRepository",
                "Erro ao buscar seller.",
                e
            )

            Result.failure(e)
        }
    }

    override suspend fun getOwnerUser(): Result<User?> {

        val firebaseUser = auth.currentUser
            ?: return Result.success(null)

        return try {

            val document = firestore
                .collection("owners")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!document.exists()) {
                return Result.success(null)
            }

            val user = document.toObject(User::class.java)
                ?: return Result.success(null)

            saveUserLocally(
                uid = firebaseUser.uid,
                user = user
            )


            Result.success(
                document.toObject(User::class.java)
            )

        } catch (e: Exception) {

            Log.d(
                "UserRepository",
                "Erro ao buscar owner.",
                e
            )

            Result.failure(e)
        }
    }


    private suspend fun saveUserLocally(
        uid: String,
        user: User
    ) {
        userDao.saveUserDao(
            UserEntity(
                uid = uid,
                email = user.email,
                ownerId = user.ownerId,
                role = user.role
            )
        )
    }


    override suspend fun getCurrentOwnerId(): Result<String> {
        val firebaseUser = auth.currentUser
            ?: return Result.failure(
                Exception("Usuário não está logado")
            )

        val userDaoResult = userDao.getUserDao(firebaseUser.uid)


        return if (userDaoResult?.ownerId == null) {
            val ownerId = userDaoResult?.uid
                ?: return Result.failure(
                    Exception("Este vendedor não possui um proprietário vinculado")
                )

            Log.d(
                "UserRepository",
                "OwnerId não encontrado no banco local, retornando o uid do usuário: $ownerId"
            )
            Result.success(ownerId)
        } else {
            Log.d(
                "UserRepository",
                "OwnerId encontrado no banco local: ${userDaoResult.ownerId}"
            )
            Result.success(userDaoResult.ownerId)
        }
    }


    override suspend fun becomeOwner(): Result<Boolean> {

        val firebaseUser = auth.currentUser
            ?: return Result.failure(
                Exception("Usuário não autenticado")
            )

        return try {

            val owner = User(
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