package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.domain.model.OwnerCode
import com.example.anotafacil.domain.repository.OwnerCodeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom

class OwnerCodeRepositoryImpl @Inject constructor(
    auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : OwnerCodeRepository {

    private val firebaseUser = auth.currentUser?.uid

    override suspend fun generateCode(): Result<OwnerCode> {
        return try {

            Log.d("OwnerCodeRepository", "Generating code for user: $firebaseUser")

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
                ownerId = firebaseUser ?: "",
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
        return try {
            Log.d("OwnerCodeRepository", "Generating code for user: $firebaseUser")

            val now = System.currentTimeMillis()

            val snapshot = firestore
                .collection("ownerCodes")
                .whereEqualTo("ownerId", firebaseUser)
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
}