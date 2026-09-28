package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.User

interface AccountProfileRepository {

    suspend fun getUserData(): Result<User>

    suspend fun deleteAccount(): Result<Boolean>
}