package com.example.anotafacil.domain.datastore

import kotlinx.coroutines.flow.Flow


interface UserDataStore {

    val pendingEmailChange: Flow<String?>

    suspend fun hasPreviouslyHadPremium(uid: String): Boolean

    suspend fun savePendingEmail(email: String)

    suspend fun savePreviouslyHadPremium(uid: String, hadPremium: Boolean)

    suspend fun clearPendingEmailChange()
}