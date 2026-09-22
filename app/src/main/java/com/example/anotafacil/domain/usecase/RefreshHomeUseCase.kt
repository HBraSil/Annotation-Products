package com.example.anotafacil.domain.usecase

import com.example.anotafacil.data.sync.SyncManager
import javax.inject.Inject

class RefreshHomeUseCase @Inject constructor(
    private val syncManager: SyncManager
) {

    suspend operator fun invoke(): Boolean {
        return syncManager.downloadAll()
    }
}