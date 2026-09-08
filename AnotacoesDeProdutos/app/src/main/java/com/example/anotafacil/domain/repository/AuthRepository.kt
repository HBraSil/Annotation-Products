package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.User


interface AuthRepository {
    suspend fun loginWithGoogle(): Result<User>
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Boolean>
    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): Result<Boolean>
}