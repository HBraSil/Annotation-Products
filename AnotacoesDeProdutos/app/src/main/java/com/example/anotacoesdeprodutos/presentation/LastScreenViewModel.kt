package com.example.anotacoesdeprodutos.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.Screens
import com.example.anotacoesdeprodutos.data.datastore.LastScreenDatastore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LastScreenViewModel @Inject constructor(
    private val lastScreenDatastore: LastScreenDatastore
): ViewModel() {

    private val _lastScreenProfile = MutableStateFlow<String?>(null)
    val lastActiveProfile = _lastScreenProfile.asStateFlow()

    init {
        viewModelScope.launch {
            lastScreenDatastore.lastScreenProfile.collect { route ->
                _lastScreenProfile.value = route ?: Screens.HOME.route
            }
        }
    }

    fun currentRoute(route: String) {
        viewModelScope.launch {
            Log.d("LastScreenViewModel", "currentRoute: $route")
            lastScreenDatastore.setLastScreenProfile(route)
        }
    }
}