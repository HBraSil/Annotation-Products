package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.SellerHomeUsers
import com.example.anotafacil.domain.model.User

interface UserRepository {
    suspend fun getOwner(): Result<User>

    suspend fun getSellerHomeUsers(): Result<SellerHomeUsers>

    suspend fun getCurrentOwner(): Result<User>

    suspend fun becomeOwner(): Result<Boolean>

    suspend fun signOut(): Result<Boolean>
}