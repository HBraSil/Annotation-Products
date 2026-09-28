package com.example.anotafacil.data.sync

import android.util.Log
import com.example.anotafacil.domain.repository.UserRepository
import javax.inject.Inject
import kotlin.uuid.Uuid



class SyncManager @Inject constructor(
    private val userRepository: UserRepository,
    private val syncUploader: SyncUploader,
    private val syncDownloader: SyncDownloader
) {

    suspend fun upload(): Result<Boolean> {
        val owner = userRepository
            .getCurrentOwner()
            .getOrElse {
                return Result.failure(it)
            }

        return if (owner.ownerId != null) Result.success(syncUploader.upload(owner.ownerId))
        else Result.failure(Exception("Proprietário não encontrado"))
    }

    suspend fun downloadAll(): Boolean {
        val owner = userRepository
            .getCurrentOwner()
            .getOrElse {
                return false
            }

        Log.d("SyncManager", "Downloading all data for owner: $owner")

        return if (owner.ownerId != null) syncDownloader.downloadAll(owner.ownerId) else false
    }

    suspend fun downloadCity(cityId: Uuid): Boolean {
        val owner = userRepository
            .getCurrentOwner()
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