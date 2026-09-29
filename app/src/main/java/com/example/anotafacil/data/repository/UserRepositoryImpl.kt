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
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
): UserRepository {


    override suspend fun getOwner(): Result<User> {

        val firebaseUserUid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Usuário não está logado"))

        return try {

            val document = firestore
                .collection("owners")
                .document(firebaseUserUid)
                .get()
                .await()

            if (!document.exists()) {
                return Result.failure(Exception("Owner não encontrado"))
            }

            val user = document.toObject(User::class.java)
                ?: return Result.failure(Exception("Erro ao converter usuário"))

            saveUserLocally(
                uid = firebaseUserUid,
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


    private suspend fun getSeller(): Result<User?> {

        val firebaseUserUid = auth.currentUser?.uid
            ?: return Result.success(null)

        return try {

            Log.d(
                "UserRepository",
                "UID autenticado: $firebaseUserUid"
            )

            val document = firestore
                .collection("sellers")
                .document(firebaseUserUid)
                .get()
                .await()

            Log.d(
                "UserRepository",
                "Seller encontrado: ${document.exists()}"
            )

            if (!document.exists()) return Result.success(null)


            val user = document.toObject(User::class.java)
                ?: return Result.success(null)

            Log.d(
                "UserRepository",
                "Seller: $user"
            )

            saveUserLocally(
                uid = firebaseUserUid,
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


    private suspend fun getOwnerById(ownerId: String): Result<User?> {
        return try {

            val document = firestore
                .collection("owners")
                .document(ownerId)
                .get()
                .await()

            if (!document.exists()) {
                return Result.success(null)
            }

            val user = document.toObject(User::class.java)
                ?: return Result.success(null)

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


    override suspend fun getSellerHomeUsers(): HomeResult {
        val sellerResult = getSeller()
        if (sellerResult.isFailure) return HomeResult.Error


        val seller = sellerResult.getOrNull() ?: return HomeResult.NotFound
        val ownerId = seller.ownerId ?: return HomeResult.Disconnected

        val ownerResult = getOwnerById(ownerId)
        if (ownerResult.isFailure) return HomeResult.OwnerError


        val owner = ownerResult.getOrNull()?: return HomeResult.OwnerNotFound

        return HomeResult.Success(
            SellerHomeUsers(
                seller = seller,
                owner = owner
            )
        )
    }


    private suspend fun saveUserLocally(
        uid: String,
        user: User
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


    override suspend fun getCurrentOwner(): Result<User> {

        return try {
            val userDaoResult = userDao.getUserDao()
                ?: return Result.failure(Exception("Usuário não encontrado no banco local"))

            Result.success(userDaoResult.toDomain())
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