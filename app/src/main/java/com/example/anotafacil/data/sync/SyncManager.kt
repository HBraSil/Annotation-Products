package com.example.anotafacil.data.sync

import android.util.Log
import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.network.RemoteDatabase
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.uuid.Uuid



class SyncManager @Inject constructor(
    private val customerDao: CustomerDao,
    private val remoteDb: RemoteDatabase,
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
            val remoteUser = remoteDb.getUserData { uid ->
                val currentUser = userRepository.getCurrentUser(uid).getOrNull()

                currentUser?.let {
                    Log.d("SyncManager", "getUserData upload: $it")
                    return@getUserData it.toEntity()
                }
            }.getOrElse {
                return Result.failure(it)
            }

            val ownerUid = when (remoteUser.role) {
                UserRole.OWNER -> remoteUser.uid
                UserRole.SELLER -> remoteUser.ownerId
            }

            if (ownerUid == null) {
                return Result.failure(Exception("Você não está mais conectado a um proprietário"))
            }

            Result.success(syncUploader.upload(ownerUid))
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
            val remoteUser = remoteDb.getUserData { uid ->
                val currentUser = userRepository.getCurrentUser(uid).getOrNull()

                currentUser?.let {
                    Log.d("SyncManager", "getUserData: $it")
                    return@getUserData it.toEntity()
                }
            }.getOrElse {
                return Result.failure(it)
            }

            val ownerUid = when (remoteUser.role) {
                UserRole.OWNER -> remoteUser.uid
                UserRole.SELLER -> remoteUser.ownerId
            }

            if (ownerUid == null) {
                return Result.failure(Exception("Você não está mais conectado a um proprietário"))
            }

            Result.success(syncDownloader.downloadAll(ownerUid))
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                return Result.failure(Exception("Sem conexão com a internet"))
            }
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

            val remoteUser = remoteDb.getUserData { uid ->
                val currentUser = userRepository.getCurrentUser(uid).getOrNull()

                currentUser?.let {
                    Log.d("SyncManager", "getUserData download: $it")
                    return@getUserData it.toEntity()
                }
            }.getOrElse {
                return Result.failure(it)
            }

            val ownerUid = when (remoteUser.role) {
                UserRole.OWNER -> remoteUser.uid
                UserRole.SELLER -> remoteUser.ownerId
            }

            if (ownerUid == null) {
                return Result.failure(Exception("Você não está mais conectado a um proprietário"))
            }

            Result.success(
                syncDownloader.downloadCityData(
                    ownerId = ownerUid,
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