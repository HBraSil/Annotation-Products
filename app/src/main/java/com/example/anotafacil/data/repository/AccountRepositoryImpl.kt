package com.example.anotafacil.data.repository

import com.example.anotafacil.domain.repository.AccountRepository
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val functions: FirebaseFunctions
) : AccountRepository {

    override suspend fun testDeleteAccount(): Result<String> {

        return try {

            val result = functions
                .getHttpsCallable("deleteMyAccount")
                .call()
                .await()

            val data = result.data as Map<*, *>

            val uid = data["uid"] as? String
                ?: throw Exception("UID não retornado pelo servidor.")

            Result.success(uid)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}