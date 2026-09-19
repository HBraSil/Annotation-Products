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

    override suspend fun getUser(): Result<User?> {

        val firebaseUser = auth.currentUser
            ?: return Result.success(null)

        return try {

            Log.d(
                "UserRepository",
                "Buscando usuário online. UID: ${firebaseUser.uid}"
            )

            var document = firestore
                .collection("sellers")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (document.exists()) {

                Log.d(
                    "UserRepository",
                    "Usuário encontrado em sellers"
                )

                val ownerData = document.data?.toMutableMap()?.apply {
                    this["role"] = UserRole.OWNER
                }

                if (ownerData != null) {
                    firestore
                        .collection("owners")
                        .document(firebaseUser.uid)
                        .set(ownerData)
                        .await()
                }

                firestore
                    .collection("sellers")
                    .document(firebaseUser.uid)
                    .delete()
                    .await()

                document = firestore
                    .collection("owners")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

            } else {

                Log.d(
                    "UserRepository",
                    "Usuário não encontrado em sellers, buscando em owners"
                )

                document = firestore
                    .collection("owners")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
            }

            val user = document.toObject(User::class.java)

            if (user != null) {
                userDao.saveUser(
                    UserEntity(
                        uid = firebaseUser.uid,
                        ownerId = user.ownerId,
                        role = user.role
                    )
                )
            }

            Log.d(
                "UserRepository",
                "Usuário encontrado: $user"
            )

            Result.success(user)

        } catch (e: Exception) {

            Log.d(
                "UserRepository",
                "Falha ao buscar usuário online. Tentando Room.",
                e
            )

            val localUser = userDao.getUser(firebaseUser.uid)

            if (localUser != null) {
                Result.success(
                    User(
                        ownerId = localUser.ownerId,
                        role = localUser.role
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }


    override suspend fun getCurrentOwnerId(): Result<String> {

        val firebaseUser = auth.currentUser?.uid
            ?: return Result.failure(Exception("Usuário não está logado"))

        Log.d(
            "UserRepository",
            "Chegou aqui antes de UserResult"
        )
        val userResult = getUser()

        Log.d(
            "UserRepository",
            "Depois de UserResult"
        )

        if (userResult.isFailure) {
            return Result.failure(
                userResult.exceptionOrNull()
                    ?: Exception("Erro ao obter usuário")
            )
        }

        val user = userResult.getOrNull()
            ?: return Result.failure(Exception("Usuário não encontrado"))

        return when (user.role) {

            UserRole.OWNER ->
                Result.success(firebaseUser)

            UserRole.SELLER ->
                Result.success(user.ownerId!!)

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