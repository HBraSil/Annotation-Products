package com.example.anotafacil.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.anotafacil.domain.datastore.UserDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.distinctUntilChanged


private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.userDatastore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)


@Singleton
class UserDataStoreImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : UserDataStore {

    private companion object {
        val PENDING_EMAIL_CHANGE = stringPreferencesKey("pending_email_change")

        fun previouslyHadPremium(uid: String) = booleanPreferencesKey("previously_had_premium_$uid")

        fun wasSellerLinked(uid: String) =
            booleanPreferencesKey("was_seller_linked_$uid")

        fun sellerDisconnectionPending(uid: String) =
            booleanPreferencesKey("seller_disconnection_pending_$uid")

        fun lastLinkedOwnerUid(uid: String) =
            stringPreferencesKey("last_linked_owner_uid_$uid")
    }

    override suspend fun updateSellerLinkState(
        uid: String,
        ownerId: String?
    ) {
        context.userDatastore.edit { preferences ->
            val linkedKey = wasSellerLinked(uid)
            val pendingKey = sellerDisconnectionPending(uid)

            val previousLinked = preferences[linkedKey] ?: false
            val isLinked = !ownerId.isNullOrBlank()

            if (previousLinked && !isLinked) {
                preferences[pendingKey] = true
            }

            if (isLinked) {
                preferences[lastLinkedOwnerUid(uid)] = ownerId
                preferences[pendingKey] = false
            }

            preferences[linkedKey] = isLinked
        }
    }

    override suspend fun getLastLinkedOwnerUid(uid: String): String? {
        return context.userDatastore.data.first()[lastLinkedOwnerUid(uid)]
    }

    override suspend fun clearSellerLinkState(uid: String) {
        context.userDatastore.edit { preferences ->
            val linkedKey = wasSellerLinked(uid)
            val pendingKey = sellerDisconnectionPending(uid)
            val ownerKey = lastLinkedOwnerUid(uid)

            preferences[linkedKey] = false
            preferences[pendingKey] = false
            preferences[ownerKey] = ""
        }
    }

    override fun observeSellerDisconnectionPending(
        uid: String
    ): Flow<Boolean> {
        return context.userDatastore.data
            .map { preferences ->
                preferences[sellerDisconnectionPending(uid)] ?: false
            }
            .distinctUntilChanged()
    }

    override suspend fun clearSellerDisconnectionPending(uid: String) {
        context.userDatastore.edit { preferences ->
            preferences[sellerDisconnectionPending(uid)] = false
        }
    }

    override suspend fun wasPremiumActive(uid: String): Boolean =
        context.userDatastore.data.first()[previouslyHadPremium(uid)] == true

    override suspend fun savePreviouslyHadPremium(uid: String, hadPremium: Boolean) {
        context.userDatastore.edit { preferences ->
            preferences[previouslyHadPremium(uid)] = hadPremium
        }
    }

    override val pendingEmailChange: Flow<String?> =
        context.userDatastore.data.map { preferences ->
            preferences[PENDING_EMAIL_CHANGE]
        }

    override suspend fun savePendingEmail(email: String) {
        context.userDatastore.edit { preferences ->
            preferences[PENDING_EMAIL_CHANGE] = email
        }
    }

    override suspend fun clearPendingEmailChange() {
        context.userDatastore.edit { preferences ->
            preferences.remove(PENDING_EMAIL_CHANGE)
        }
    }
}