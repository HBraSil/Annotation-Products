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
        val ownerId = userRepository
            .getCurrentOwnerId()
            .getOrElse {
                return Result.failure(it)
            }

        return Result.success(syncUploader.upload(ownerId))
    }

    suspend fun downloadAll(): Boolean {
        val ownerId = userRepository
            .getCurrentOwnerId()
            .getOrElse {
                return false
            }

        Log.d("SyncManager", "Downloading all data for owner: $ownerId")

        return syncDownloader.downloadAll(ownerId)
    }

    suspend fun downloadCity(cityId: Uuid): Boolean {
        val ownerId = userRepository
            .getCurrentOwnerId()
            .getOrElse {
                return false
            }

        return syncDownloader.downloadCityData(
            ownerId = ownerId,
            cityId = cityId
        )
    }
}