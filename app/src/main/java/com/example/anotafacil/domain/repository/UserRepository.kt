package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.User

interface UserRepository {
    suspend fun getSellerUser(): Result<User?>

    suspend fun getOwnerUser(): Result<User?>

    suspend fun getCurrentOwnerId(): Result<String>

    suspend fun becomeOwner(): Result<Boolean>

    suspend fun signOut(): Result<Boolean>
}