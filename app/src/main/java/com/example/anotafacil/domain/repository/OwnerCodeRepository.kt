package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.OwnerCode

interface OwnerCodeRepository {

    suspend fun generateCode(): Result<OwnerCode>

    suspend fun getActiveCode(): Result<OwnerCode>

    suspend fun verifyCode(code: String, name: String): Result<Boolean>

}