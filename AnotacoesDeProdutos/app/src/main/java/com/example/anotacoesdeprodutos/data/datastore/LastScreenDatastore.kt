package com.example.anotacoesdeprodutos.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.anotacoesdeprodutos.Screens
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
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
        Log.d("LastScreenDatastore", "recebi qual route? R:${preferences[LAST_SCREEN_PROFILE]}")
        preferences[LAST_SCREEN_PROFILE] ?: Screens.HOME.route
    }

    suspend fun setLastScreenProfile(route: String) {
        context.navigationDataStore.edit { preferences ->
            Log.d("LastScreenDatastore", "recebo qual route? R:$route")
            preferences[LAST_SCREEN_PROFILE] = route
        }


        Log.d("LastScreenDatastore", "rota salva? R: ${context.navigationDataStore.data.map { preferences ->
            preferences[LAST_SCREEN_PROFILE]
            }.first()}")
    }
}