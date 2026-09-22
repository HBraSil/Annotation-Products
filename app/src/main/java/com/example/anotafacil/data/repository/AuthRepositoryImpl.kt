package com.example.anotafacil.data.repository

import android.util.Log
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
            return Result.failure(Exception("Sem conexão com internet"))
        }

        return try {
            googleSignInUtils.doGoogleSingIn().fold(
                onSuccess = {
                    val authResult = firebaseAuth.signInWithCredential(it).await()

                    val firebaseUser = authResult.user
                    val uid = firebaseUser?.uid ?: return Result.failure(Exception("Erro ao fazer login"))

                    val sellerUser = firestore.collection("sellers")
                        .document(uid)
                        .get()
                        .await()

                    val ownerUser = firestore.collection("owners")
                        .document(uid)
                        .get()
                        .await()

                    if (!sellerUser.exists() && !ownerUser.exists()) {
                        Log.d("AuthRepository", "Usuário não existe no Firestore, criando...: ${firebaseUser.displayName}, ${firebaseUser.email}")
                        val newUser = User(
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                        )

                        firestore.collection("sellers")
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
            return Result.failure(Exception("Sem conexão com internet"))
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
            return Result.failure(Exception("Sem conexão com internet"))
        }

        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val firebaseUserUid = authResult.user?.uid
                ?: return Result.failure(Exception("Erro ao criar usuário"))

            firestore.collection("sellers")
                .document(firebaseUserUid)
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