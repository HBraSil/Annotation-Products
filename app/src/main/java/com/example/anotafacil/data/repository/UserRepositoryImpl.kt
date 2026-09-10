package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): UserRepository {
    override suspend fun getUser(): Result<User?> {
        return try {
            val firebaseUser = auth.currentUser ?: return Result.success(null)

            Log.d("UserRepository", "Buscando usuário com UID: ${firebaseUser.uid}")
            val document = firestore
                .collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!document.exists()) {
                Log.d("UserRepository", "Usuário não encontrado no Firestore")
                return Result.success(null)
            }

            val user = document.toObject(User::class.java)

            Log.d("UserRepository", "Usuário encontrado: $user")

            Result.success(user)

        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao buscar usuário", e)
            Result.failure(e)
        }
    }
}