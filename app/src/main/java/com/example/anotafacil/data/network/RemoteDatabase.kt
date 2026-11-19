package com.example.anotafacil.data.network

import android.util.Log
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.firestore.FirestoreSchema
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class RemoteDatabase @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getUserData(): Result<User> {
        return try {
            val firebaseUserUid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuário não autenticado"))

            val document = firestore
                .collection(FirestoreSchema.Collections.USERS)
                .document(firebaseUserUid)
                .get()
                .await()

            if (!document.exists()) return Result.failure(Exception("Usuário não encontrado"))


            val user = document.toObject(User::class.java)
                ?: return Result.failure(Exception("Usuário inválido"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}