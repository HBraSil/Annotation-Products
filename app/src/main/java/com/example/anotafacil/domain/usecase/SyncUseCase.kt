package com.example.anotafacil.domain.usecase

import com.example.anotafacil.data.sync.SyncManager
import jakarta.inject.Inject

class SyncUseCase @Inject constructor(
    private val syncManager: SyncManager
) {
    suspend operator fun invoke(): Boolean = syncManager.sync()
}