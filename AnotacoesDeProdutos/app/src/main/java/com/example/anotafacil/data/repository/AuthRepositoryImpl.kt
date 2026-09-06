package com.example.anotafacil.data.repository

import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            val uid = firebaseUser?.uid ?: return Result.failure(Exception("Erro ao fazer login"))

            val userSnapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (!userSnapshot.exists()) {
                return Result.failure(Exception("Usuário não encontrado"))
            }

            val user = userSnapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("Erro ao converter usuário"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            val uid = firebaseUser?.uid ?: return Result.failure(Exception("Erro ao fazer login"))
            val userSnapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (!userSnapshot.exists()) {
                return Result.failure(Exception("Usuário não encontrado"))
            }
            val user = userSnapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("Erro ao converter usuário"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}