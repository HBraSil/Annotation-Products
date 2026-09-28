package com.example.anotafacil.data.network

import android.util.Log
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
    suspend fun getUserData(getUserRole: suspend (String) -> UserRole?): Result<User> {
        return try {
            val firebaseUserUid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuário não autenticado"))

            Log.d("RemoteDatabase", "firebaseUserUid: $firebaseUserUid, getUserRole: $getUserRole")
            val userRole = getUserRole(firebaseUserUid)
                ?: return Result.failure(Exception("Tipo de usuário não encontrado"))

            val collectionPath = when (userRole) {
                UserRole.OWNER -> "owners"
                UserRole.SELLER -> "sellers"
            }

            val document = firestore
                .collection(collectionPath)
                .document(firebaseUserUid)
                .get()
                .await()

            if (!document.exists()) {
                return Result.failure(Exception("Usuário não encontrado"))
            }

            val user = document.toObject(User::class.java)
                ?: return Result.failure(Exception("Usuário inválido"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}