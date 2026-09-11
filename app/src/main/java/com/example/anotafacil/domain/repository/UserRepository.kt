package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.User

interface UserRepository {
    suspend fun getUser(): Result<User?>
    suspend fun signOut(): Result<Boolean>
}