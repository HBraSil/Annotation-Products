package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.exception.SellerConnectionState
import com.example.anotafacil.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun observeOwnerInfo(ownerUid: String? = null): Flow<HomeResult>

    fun loadSellerAndOwnerInfo(): Flow<HomeResult>

    fun observeSellerConnectionWithOwner(): Flow<SellerConnectionState>


    suspend fun getCurrentUser(uid: String? = null): Result<User>

    fun observeCurrentUser(): Flow<Result<User>>

    suspend fun becomeOwner(): Result<Boolean>

    suspend fun verifyingAndDeletingOwnerFromSeller(): Result<Boolean>

    suspend fun clearData(): Result<Boolean>

    suspend fun saveChanges(newUserName: User? = null, newUserEmail: User? = null): Result<String?>

    suspend fun checkEmailChange(): Result<Boolean>

    suspend fun signOut(): Result<Boolean>
}
