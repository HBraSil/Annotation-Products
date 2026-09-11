package com.example.anotafacil.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
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

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val user: User? = null
)