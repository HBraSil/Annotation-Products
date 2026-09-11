package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.model.OwnerCode
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.repository.OwnerCodeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom

class OwnerCodeRepositoryImpl @Inject constructor(
    auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : OwnerCodeRepository {

    private val firebaseUserUid = auth.currentUser?.uid

    override suspend fun generateCode(): Result<OwnerCode> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val random = SecureRandom()

            var code: String

            do {
                code = random.nextInt(1_000_000)
                    .toString()
                    .padStart(6, '0')

                val document = firestore
                    .collection("ownerCodes")
                    .document(code)
                    .get()
                    .await()

            } while (document.exists())

            val now = System.currentTimeMillis()

            val ownerCode = OwnerCode(
                ownerId = firebaseUserUid ?: "",
                code = code,
                createdAt = now,
                expiresAt = now + 5 * 60 * 1000
            )

            firestore
                .collection("ownerCodes")
                .document(code)
                .set(ownerCode)
                .await()

            Result.success(ownerCode)

        } catch (e: Exception) {
            Log.e("OwnerCodeRepository", "Error generating code: ${e.message} e = ${e.cause} e = ${e.stackTrace}")
            Result.failure(Exception(e.message))
        }
    } // the supplied auth credential is incorrect, malformed or has expired.


    override suspend fun getActiveCode(): Result<OwnerCode> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            Log.d("OwnerCodeRepository", "Generating code for user: $firebaseUserUid")

            val now = System.currentTimeMillis()

            val snapshot = firestore
                .collection("ownerCodes")
                .whereEqualTo("ownerId", firebaseUserUid)
                .whereGreaterThan("expiresAt", now)
                .limit(1)
                .get()
                .await()

            val document = snapshot.documents.firstOrNull()
            val ownerCode =
                document?.toObject(OwnerCode::class.java) ?: return Result.failure(Exception())

            Result.success(ownerCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyCode(code: String, name: String): Result<Boolean> {
        return try {
            isOwner()
                    .onSuccess { isOwner ->
                        if (!isOwner) {
                            val document = firestore
                                .collection("ownerCodes")
                                .document(code)
                                .get()
                                .await()

                            if (!document.exists()) {
                                return Result.failure(Exception("Código não encontrado"))
                            }

                            val ownerCode = document.toObject(OwnerCode::class.java)
                                ?: return Result.failure(Exception("Código não encontrado"))


                            if (System.currentTimeMillis() >= ownerCode.expiresAt) {
                                Log.d("OwnerCodeRepository", "Code expired")
                                return Result.failure(
                                    Exception("Código expirado.")
                                )
                            }

                            return linkSellerWithOwner(ownerCode, name)
                        }

                        return Result.success(false)
                    }
                .onFailure {
                    return Result.failure(it)
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private suspend fun linkSellerWithOwner(
        ownerCode: OwnerCode,
        name: String
    ): Result<Boolean> {

        Log.d("OwnerCodeRepository", "Linking seller with owner")
        return try {
            val sellerReference = firestore
                .collection("owners")
                .document(ownerCode.ownerId)
                .get()
                .await()

            if (!sellerReference.exists()) {
                Log.d("OwnerCodeRepository", "Seller not found")
                return Result.failure(Exception("Código inválido."))
            }

            val seller = User(
                name = name,
                ownerId = ownerCode.ownerId,
                role = UserRole.SELLER
            )

            firestore
                .collection("sellers")
                .document(ownerCode.ownerId)
                .set(seller)
                .await()

            Log.d("OwnerCodeRepository", "SALVO COM SUCESSO")

            Result.success(false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun isOwner(): Result<Boolean> {
        return try {
            val uid = firebaseUserUid
                ?: return Result.failure(
                    Exception("Usuário não autenticado.")
                )

            val document = firestore
                .collection("owners")
                .document(uid)
                .get()
                .await()

            Result.success(document.exists())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}