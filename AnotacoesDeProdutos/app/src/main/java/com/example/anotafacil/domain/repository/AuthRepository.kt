package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.User


interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
}