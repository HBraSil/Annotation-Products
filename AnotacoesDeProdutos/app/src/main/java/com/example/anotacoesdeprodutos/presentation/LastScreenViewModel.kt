package com.example.anotacoesdeprodutos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.data.datastore.LastScreenDatastore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LastScreenViewModel @Inject constructor(
    private val lastScreenDatastore: LastScreenDatastore
): ViewModel() {

    val lastActiveProfile = lastScreenDatastore.lastScreenProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )


    fun lastRoute(route: String) {
        viewModelScope.launch {
            lastScreenDatastore.setLastScreenProfile(route)
        }
    }
}