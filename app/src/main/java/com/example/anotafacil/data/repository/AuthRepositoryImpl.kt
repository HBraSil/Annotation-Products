package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.firestore.FirestoreSchema
import com.example.anotafacil.data.firestore.model.FirestoreUser
import com.example.anotafacil.data.util.GoogleSignInUtils
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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

                    val userRef  = firestore
                        .collection(FirestoreSchema.Collections.USERS)
                        .document(uid)

                    val userSnapshot = userRef
                        .get(Source.SERVER)
                        .await()


                    val remoteUser = if (userSnapshot.exists()) {
                        userSnapshot.toObject(FirestoreUser::class.java)
                            ?: throw Exception(
                                "Erro ao recuperar os dados do usuário"
                            )
                    } else {
                        val newUser = FirestoreUser(
                            //uid = uid,
                            name = firebaseUser.displayName.orEmpty(),
                            email = firebaseUser.email.orEmpty(),
                            ownerId = null
                        )

                        userRef.set(newUser).await()
                        newUser
                    }

                    // 3. Consultar o acesso de proprietário
                    val entitlementSnapshot = firestore
                        .collection(
                            FirestoreSchema.Collections.SUBSCRIPTION_ENTITLEMENTS
                        )
                        .document(uid)
                        .get(Source.SERVER)
                        .await()

                    val hasOwnerAccess = entitlementSnapshot.getBoolean(
                        FirestoreSchema.SubscriptionFields.HAS_OWNER_ACCESS
                    ) == true

                    Log.d("AuthRepositoryImpl", "loginWithGoogle: $hasOwnerAccess")

                    // 4. Determinar o papel atual
                    val role: UserRole? = when {
                        hasOwnerAccess -> UserRole.OWNER
                        !remoteUser.ownerId.isNullOrBlank() -> UserRole.SELLER
                        else -> null
                    }

                    // 5. Construir o usuário do aplicativo
                    val user = User(
                        uid = uid,
                        name = remoteUser.name,
                        email = remoteUser.email,
                        ownerId = remoteUser.ownerId,
                        role = role
                    )

                    userDao.saveUserDao(user.toEntity())

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


            firestore.collection(FirestoreSchema.Collections.USERS)
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

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.setLanguageCode("pt")

            firebaseAuth.sendPasswordResetEmail(email.trim()).await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}