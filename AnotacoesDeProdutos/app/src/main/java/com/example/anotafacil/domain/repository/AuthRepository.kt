package com.example.anotafacil.domain.repository


interface AuthRepository {
    suspend fun loginWithGoogle(): Result<Boolean>
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Boolean>
    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): Result<Boolean>
}