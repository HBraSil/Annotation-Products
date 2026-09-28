package com.example.anotafacil.data.repository

import android.util.Log
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.toDomain
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.data.network.RemoteDatabase
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.AccountProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.logger.Logger
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountProfileProfileRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val remoteDb: RemoteDatabase,
    private val firebaseFunctions: FirebaseFunctions,
    private val appDatabase: AppDatabase
) : AccountProfileRepository {

    override suspend fun getUserData(): Result<User> {
        val user = userDao.getUserDao()
            ?: return Result.failure(Exception("Usuário não encontrado"))

        return Result.success(user.toDomain())
    }


    override suspend fun deleteAccount(): Result<Boolean> {
        return try {
            firebaseFunctions
                .getHttpsCallable("deleteMyAccount")
                .call()
                .await()


            appDatabase.clearAllData()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}