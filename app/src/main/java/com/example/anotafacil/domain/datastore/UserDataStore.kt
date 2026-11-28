package com.example.anotafacil.domain.datastore

import kotlinx.coroutines.flow.Flow


interface UserDataStore {

    val pendingEmailChange: Flow<String?>

    suspend fun wasPremiumActive(uid: String): Boolean

    suspend fun savePendingEmail(email: String)

    suspend fun savePreviouslyHadPremium(uid: String, hadPremium: Boolean)

    suspend fun clearPendingEmailChange()

    suspend fun updateSellerLinkState(
        uid: String,
        ownerId: String?
    )

    suspend fun getLastLinkedOwnerUid(uid: String): String?

    suspend fun clearSellerLinkState(uid: String)

    fun observeSellerDisconnectionPending(
        uid: String
    ): Flow<Boolean>

    suspend fun clearSellerDisconnectionPending(uid: String)

    suspend fun markSellerDisconnectionInitiatedBySeller(uid: String)

    suspend fun clearSellerDisconnectionInitiatedBySeller(uid: String)

    fun observeSellerDisconnectionBySellerPending(uid: String): Flow<Boolean>
}