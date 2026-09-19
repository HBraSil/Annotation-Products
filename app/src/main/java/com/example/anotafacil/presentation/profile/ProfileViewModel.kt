package com.example.anotafacil.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.domain.usecase.SyncUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val syncUseCase: SyncUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()


    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            userRepository.getUser()
                .onSuccess { user ->

                    _uiState.update { it.copy(user = user) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            error = exception.message ?: "Não foi possível obter o usuário."
                        )
                    }
                }
        }
    }

    fun onSyncCloudClick() {
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            val result = syncUseCase()
            Log.d("ProfileViewModel", "Resultado da sincronização: $result")
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    success = result
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}

data class ProfileUiState(
    val isSyncing: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val user: User? = null
)