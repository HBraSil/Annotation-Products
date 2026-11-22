package com.example.anotafacil.data.sync

import android.util.Log
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.Uuid



class SyncManager @Inject constructor(
    private val userRepository: UserRepository,
    private val syncUploader: SyncUploader,
    private val syncDownloader: SyncDownloader,
    private val networkChecker: NetworkChecker
) {

    suspend fun upload(): Result<Boolean> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val currentLocalUser = userRepository.getCurrentUser().getOrNull()
                ?: return Result.failure(Exception("Usuário não encontrado"))


            val ownerId = currentLocalUser.currentOwnerUid ?: return Result.failure(Exception("Você não está mais conectado a um proprietário"))

            Result.success(syncUploader.upload(ownerId))
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                return Result.failure(Exception("Sem conexão com a internet"))
            }
            Result.failure(Exception("Erro ao acessar o banco de dados remoto: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }

    }



    suspend fun downloadAll(): Result<Boolean> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val currentLocalUser = userRepository.getCurrentUser().getOrNull()
                ?: return Result.failure(Exception("Usuário não encontrado"))

            Log.d("SyncManager", "currentLocalUser: $currentLocalUser")
            val ownerId = currentLocalUser.currentOwnerUid ?: return Result.failure(Exception("Você não está mais conectado a um proprietário"))

            Result.success(syncDownloader.downloadAll(ownerId))
        } catch (e: CancellationException) {
            Log.d("Fail", "CAIU AQUI DOWNLOAD: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                return Result.failure(Exception("Sem conexão com a internet"))
            }
            Log.d("Fail", "CAIU 12 DOWNLOAD: ${e.message}")

            Result.failure(Exception("Erro ao acessar o banco de dados remoto: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun downloadCity(cityId: Uuid): Result<Boolean> {
        if (!networkChecker.hasInternetConnection()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }


        return try {

            val remoteUser = userRepository.getCurrentUser().getOrElse {
                return Result.failure(it)
            }

            val ownerId = remoteUser.currentOwnerUid ?: return Result.failure(Exception("Você não está mais conectado a um proprietário"))

            Log.d("SyncManager", "currentLocalUser: $remoteUser")

            Result.success(
                syncDownloader.downloadCityData(
                    ownerId = ownerId,
                    cityId = cityId
                )
            )
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                return Result.failure(Exception("Sem conexão com a internet"))
            }
            Result.failure(Exception("Erro ao acessar o banco de dados remoto: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}