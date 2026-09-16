package com.example.anotafacil.domain.repository

interface AccountRepository {
    suspend fun testDeleteAccount(): Result<String>
}