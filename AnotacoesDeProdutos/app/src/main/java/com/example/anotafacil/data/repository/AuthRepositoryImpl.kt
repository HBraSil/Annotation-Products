package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.util.GoogleSignInUtils
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInUtils: GoogleSignInUtils
): AuthRepository {
    override suspend fun loginWithGoogle(): Result<User> {
        return try {
            return googleSignInUtils.doGoogleSingIn().fold(
                onSuccess = {

                    Log.d("AuthRepositoryImpl", "Login com Google bem-sucedido: ${it.provider}")
                    val authResult = firebaseAuth.signInWithCredential(it).await()
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
                },
                onFailure = {
                    Result.failure(Exception("Falha ao fazer login com Google"))
                }
            )
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