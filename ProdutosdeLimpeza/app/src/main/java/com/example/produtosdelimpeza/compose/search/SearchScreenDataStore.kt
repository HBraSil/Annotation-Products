package com.example.produtosdelimpeza.compose.search

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val SEARCH_HISTORY_KEY = "search_history"

class SearchScreenDataStore (private val dataStore: DataStore<Preferences>) {

    private val searcHistoryKey = stringSetPreferencesKey(SEARCH_HISTORY_KEY)

    suspend fun saveSearchHistory(history: Set<String>) {
        dataStore.edit {
            it[searcHistoryKey] = history
        }
    }

    suspend fun getSearchHistory(): Set<String> {
        return dataStore.data.map { preferences ->
            preferences[searcHistoryKey] ?: emptySet()
        }.first()
    }
}

val Context.searchScreenDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_screen_prefs")