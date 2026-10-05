package com.example.anotafacil.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.data.datastore.LastScreenDatastore
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val lastScreenDatastore: LastScreenDatastore
): ViewModel() {

    val lastActiveProfile = lastScreenDatastore.lastScreenProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _sellerConnection = MutableStateFlow<HomeResult>(HomeResult.NotAuthenticated)

    val sellerConnection: StateFlow<HomeResult> = _sellerConnection.asStateFlow()


    init {
        observeSellerConnection()
    }


    private fun observeSellerConnection() {
        viewModelScope.launch {
            userRepository
                .observeSellerConnection()
                .collect { result ->
                    _sellerConnection.value = result
                }
        }
    }


    fun lastRoute(route: String) {
        viewModelScope.launch {
            lastScreenDatastore.setLastScreenProfile(route)
        }
    }

    fun confirmSellerDisconnected() {
        viewModelScope.launch {
            userRepository.verifyingIfSellerCanDisconnect()
            userRepository.signOut()
            _sellerConnection.value = HomeResult.NotAuthenticated
        }
    }
}