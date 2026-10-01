package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getOwner(ownerUid: String? = null): Flow<HomeResult>

    fun getSellerData(): Flow<HomeResult>

    suspend fun getCurrentUser(uid: String? = null): Result<User>

    suspend fun becomeOwner(): Result<Boolean>

    suspend fun signOut(): Result<Boolean>

    suspend fun verifyingIfSellerCanDisconnect(): Result<Boolean>

    suspend fun clearSellerData(): Result<Boolean>
}