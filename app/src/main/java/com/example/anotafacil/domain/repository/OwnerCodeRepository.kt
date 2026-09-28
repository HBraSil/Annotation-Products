package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.OwnerCode
import com.example.anotafacil.domain.model.User
import kotlinx.coroutines.flow.Flow

interface OwnerCodeRepository {

    suspend fun generateCode(): Result<OwnerCode>

    suspend fun getActiveCode(): Result<OwnerCode>

    suspend fun verifyCode(code: String): Result<Boolean>

    fun getSellersConnected(): Flow<Result<List<User>>>

    suspend fun disconnectSeller(sellerUid: String): Result<Boolean>

}