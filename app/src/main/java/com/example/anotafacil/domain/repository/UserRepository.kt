package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.exception.ConfirmEmailChangeResult
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getOwner(ownerUid: String? = null): Flow<HomeResult>

    fun getSellerData(): Flow<HomeResult>

    fun observeSellerConnection(): Flow<HomeResult>

    suspend fun getCurrentUser(uid: String? = null): Result<User>

    suspend fun becomeOwner(): Result<Boolean>

    suspend fun verifyingIfSellerCanDisconnect(): Result<Boolean>

    //suspend fun clearSellerData(): Result<Boolean>

    suspend fun saveChanges(newUserName: User? = null, newUserEmail: User? = null): Result<String?>

    suspend fun checkEmailChange(): Result<Boolean>

    suspend fun signOut(): Result<Boolean>
}
