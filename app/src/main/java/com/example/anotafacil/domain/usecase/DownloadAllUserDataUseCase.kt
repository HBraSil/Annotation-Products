package com.example.anotafacil.domain.usecase

import com.example.anotafacil.data.sync.SyncManager
import javax.inject.Inject

class DownloadAllUserDataUseCase @Inject constructor(
    private val syncManager: SyncManager
) {

    suspend operator fun invoke(): Result<Boolean> {
        return syncManager.downloadAll()
    }
}