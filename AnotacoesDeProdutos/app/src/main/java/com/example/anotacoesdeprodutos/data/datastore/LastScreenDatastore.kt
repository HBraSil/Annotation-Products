package com.example.anotacoesdeprodutos.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private const val NAVIGATION_PREFERENCES_NAME = "profile_mode"
val Context.navigationDataStore: DataStore<Preferences> by preferencesDataStore(name = NAVIGATION_PREFERENCES_NAME)
@Singleton
class LastScreenDatastore @Inject constructor(@param:ApplicationContext private val context: Context) {
    companion object {
        val LAST_SCREEN_PROFILE = stringPreferencesKey("last_screen")
    }

    val lastScreenProfile = context.navigationDataStore.data.map { preferences ->
        preferences[LAST_SCREEN_PROFILE]
    }

    suspend fun setLastScreenProfile(route: String) {
        context.navigationDataStore.edit { preferences ->
            preferences[LAST_SCREEN_PROFILE] = route
        }
    }
}