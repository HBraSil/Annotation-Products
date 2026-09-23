package com.example.anotafacil.domain.repository

interface AccountProfileRepository {
    suspend fun deleteAccount(): Result<Boolean>
}