package com.example.anotafacil.domain.datastore

import kotlinx.coroutines.flow.Flow


interface UserDataStore {

    val pendingEmailChange: Flow<String?>

    suspend fun savePendingEmail(email: String)

    suspend fun clearPendingEmailChange()
}