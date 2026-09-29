package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getOwner(): Result<User>

    fun getUsersForSellerHome(): Flow<HomeResult>

    suspend fun getCurrentUser(uid: String? = null): Result<User>

    suspend fun becomeOwner(): Result<Boolean>

    suspend fun signOut(): Result<Boolean>
}