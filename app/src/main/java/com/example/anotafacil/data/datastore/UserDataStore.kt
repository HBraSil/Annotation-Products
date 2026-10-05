package com.example.anotafacil.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.anotafacil.domain.datastore.UserDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton


private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)


@Singleton
class UserDataStoreImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : UserDataStore {

    private companion object {
        val PENDING_EMAIL_CHANGE =
            stringPreferencesKey("pending_email_change")
    }

    override val pendingEmailChange: Flow<String?> =
        context.userDataStore.data.map { preferences ->
            preferences[PENDING_EMAIL_CHANGE]
        }

    override suspend fun savePendingEmail(email: String) {
        context.userDataStore.edit { preferences ->
            preferences[PENDING_EMAIL_CHANGE] = email
        }
    }

    override suspend fun clearPendingEmailChange() {
        context.userDataStore.edit { preferences ->
            preferences.remove(PENDING_EMAIL_CHANGE)
        }
    }
}