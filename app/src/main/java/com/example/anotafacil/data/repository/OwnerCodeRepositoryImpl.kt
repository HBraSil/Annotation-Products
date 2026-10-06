package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.model.OwnerCode
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.OwnerCodeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom

class OwnerCodeRepositoryImpl @Inject constructor(
    auth: FirebaseAuth,
    private val userDao: UserDao,
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
                expiresAt = now + 2 * 60 * 1000
            )

            firestore
                .collection("ownerCodes")
                .document(code)
                .set(ownerCode)
                .await()

            Result.success(ownerCode)

        } catch (e: Exception) {
            Log.e(
                "OwnerCodeRepository",
                "Error generating code: ${e.message} e = ${e.cause} e = ${e.stackTrace}"
            )
            Result.failure(Exception(e.message))
        }
    }


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


    override suspend fun verifyCode(code: String): Result<Boolean> {
        return try {
            val owner = currentUserIsOwner()

            if (owner == null) {
                val ownerCode = isCodeWorking(code).getOrElse { trowable ->
                    return Result.failure(trowable)
                }

                return linkSellerWithOwner(ownerCode)
            }

            return Result.success(false)
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> {
                    Result.failure(Exception("Sem conexão com a internet"))
                }

                FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                    Result.failure(Exception("Sem permissão para acessar o banco de dados"))
                }

                else -> {
                    Result.failure(e)
                }
            }
        }  catch (e: Exception) {
            Result.failure(e)
        }
    }


    private suspend fun currentUserIsOwner(): User?{
        val uid = firebaseUserUid ?: return null


        val userTest = firestore.collection("sellers")
            .document(uid)
            .get()
            .await()

        Log.d("OwnerCodeRepository", "userTest: ${userTest.toObject(User::class.java)}")


        val document = firestore
            .collection("owners")
            .document(uid)
            .get()
            .await()

        val owner = document.toObject(User::class.java)

        return owner
    }


    private suspend fun isCodeWorking(code: String): Result<OwnerCode> {
        val document = firestore
            .collection("ownerCodes")
            .document(code)
            .get()
            .await()

        if (!document.exists()) return Result.failure(Exception("Código não encontrado"))


        val ownerCode = document.toObject(OwnerCode::class.java)
            ?: return Result.failure(Exception("Código não encontrado"))


        if (System.currentTimeMillis() >= ownerCode.expiresAt)
            return Result.failure(Exception("Código expirado."))

        return Result.success(ownerCode)
    }


    private suspend fun linkSellerWithOwner(
        ownerCode: OwnerCode,
    ): Result<Boolean> {

        return try {

            val sellerUid = firebaseUserUid
                ?: return Result.failure(Exception("Usuário não autenticado."))

            firestore
                .collection("sellers")
                .document(sellerUid)
                .update("ownerId", ownerCode.ownerId)
                .await()

            Log.d(
                "OwnerCodeRepository",
                "Seller vinculado ao owner: ${ownerCode.ownerId}"
            )

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun getSellersConnected(): Flow<Result<List<User>>> {
        return firestore
            .collection("sellers")
            .whereEqualTo("ownerId", firebaseUserUid)
            .snapshots()
            .map { snapshot ->
                Log.d("HellersViewModel", "Repository caiu aqui gertSelt")
                Result.success(
                    snapshot.toObjects(User::class.java)
                )
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }


    override suspend fun disconnectSeller(sellerUid: String): Result<Boolean> {
        return try {
            firestore
                .collection("sellers")
                .document(sellerUid)
                .update("ownerId", null)
                .await()


            userDao.updateOwnerId(sellerUid, null)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}