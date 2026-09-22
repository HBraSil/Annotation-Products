package com.example.anotafacil.domain.usecase


import com.example.anotafacil.data.sync.SyncManager
import kotlin.uuid.Uuid
import javax.inject.Inject

class RefreshCustomersUseCase @Inject constructor(
    private val syncManager: SyncManager
) {

    suspend operator fun invoke(cityId: Uuid): Boolean {
        return syncManager.downloadCity(cityId)
    }
}