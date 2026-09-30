package com.example.anotafacil.data.sync

import android.util.Log
import com.example.anotafacil.data.network.RemoteDatabase
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.UserRepository
import javax.inject.Inject
import kotlin.uuid.Uuid



class SyncManager @Inject constructor(
    private val remoteDb: RemoteDatabase,
    private val userRepository: UserRepository,
    private val syncUploader: SyncUploader,
    private val syncDownloader: SyncDownloader
) {

    suspend fun upload(): Result<Boolean> {

        val remoteUser = remoteDb.getUserData { uid ->
            val currentUser = userRepository.getCurrentUser(uid).getOrNull()

            currentUser?.let {
                return@getUserData it.toEntity()
            }
        }.getOrElse {
            return Result.failure(it)
        }

        val ownerUid = when(remoteUser.role) {
            UserRole.OWNER -> remoteUser.uid
            UserRole.SELLER -> remoteUser.ownerId
        }

        return if (ownerUid != null) {
            Log.d("SyncManager", "Uploading data for owner: ${remoteUser.name} -- ${remoteUser.uid} -- ${remoteUser.ownerId}")
            Result.success(syncUploader.upload(ownerUid))
        }
        else Result.failure(Exception("Você não está mais conectado a um proprietário"))
    }

    suspend fun downloadAll(): Result<Boolean> {
        val owner = userRepository
            .getCurrentUser()
            .getOrElse {
                return Result.failure(it)
            }

        Log.d("SyncManager", "Downloading all data for owner: $owner")

        return if (owner.ownerId != null) Result.success(syncDownloader.downloadAll(owner.ownerId))
        else Result.failure(Exception("Você não está mais conectado a um proprietário"))
    }

    suspend fun downloadCity(cityId: Uuid): Boolean {
        val owner = userRepository
            .getCurrentUser()
            .getOrElse {
                return false
            }

        return if (owner.ownerId != null) syncDownloader.downloadCityData(
            ownerId = owner.ownerId,
            cityId = cityId
        )
        else false
    }
}