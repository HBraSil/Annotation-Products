package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.util.GoogleSignInUtils
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
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
                    if (it == null) return Result.failure(Exception())
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

                    val newUser = User(uid = uid, name = firebaseUser.displayName ?: "", email = firebaseUser.email ?: "")

                    val resolvedUser = when {
                        ownerUser.exists() -> {
                            val remoteOwner = ownerUser.toObject(User::class.java) ?: newUser
                            remoteOwner.copy(
                                uid = uid,
                                name = remoteOwner.name.ifBlank { firebaseUser.displayName ?: "" },
                                email = remoteOwner.email.ifBlank { firebaseUser.email ?: "" },
                                role = UserRole.OWNER,
                                ownerId = null
                            )
                        }

                        sellerUser.exists() -> {
                            val remoteSeller = sellerUser.toObject(User::class.java) ?: newUser
                            remoteSeller.copy(
                                uid = uid,
                                name = remoteSeller.name.ifBlank { firebaseUser.displayName ?: "" },
                                email = remoteSeller.email.ifBlank { firebaseUser.email ?: "" },
                                role = UserRole.SELLER,
                                ownerId = remoteSeller.ownerId
                            )
                        }

                        else -> newUser.copy(
                            role = UserRole.SELLER,
                            ownerId = null
                        )
                    }

                    if (!sellerUser.exists() && !ownerUser.exists()) {
                        firestore.collection("sellers")
                            .document(uid)
                            .set(resolvedUser)
                            .await()
                    }

                    userDao.saveUserDao(resolvedUser.toEntity())

                    Result.success(true)
                },
                onFailure = {
                    Result.failure(it)
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

            //val

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

            val user = User(uid = firebaseUserUid, name = name, email = email)


            firestore.collection("sellers")
                .document(firebaseUserUid)
                .set(user)
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