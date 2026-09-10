package com.example.anotafacil.data.repository

import com.example.anotafacil.data.util.GoogleSignInUtils
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInUtils: GoogleSignInUtils,
    private val networkChecker: NetworkChecker
): AuthRepository {
    override suspend fun loginWithGoogle(): Result<Boolean> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            googleSignInUtils.doGoogleSingIn().fold(
                onSuccess = {
                    val authResult = firebaseAuth.signInWithCredential(it).await()

                    val firebaseUser = authResult.user
                    val uid = firebaseUser?.uid ?: return Result.failure(Exception("Erro ao fazer login"))

                    val userSnapshot = firestore.collection("users")
                        .document(uid)
                        .get()
                        .await()

                    if (!userSnapshot.exists()) {
                        val newUser = User(
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(newUser)
                            .await()
                    }

                    Result.success(true)
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
    ): Result<Boolean> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            if (authResult.user?.uid == null)
                return Result.failure(Exception("Erro ao fazer login"))

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ): Result<Boolean> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Erro ao criar usuário"))

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(
                    User(name = name, email = email)
                )
                .await()

            Result.success(true)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("E-mail já cadastrado"))
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}