package com.example.anotafacil.presentation.profile.account_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.AccountProfileRepository
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AccountProfileViewModel @Inject constructor(
    private val accountProfileRepository: AccountProfileRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState = _uiState.asStateFlow()


    fun deleteAccount() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            val result = accountProfileRepository.deleteAccount()

            result
                .onSuccess {
                    signOut()
                }
                .onFailure { error ->
                    Log.d("AccountProfileViewModel", "Falha ao deletar a conta.")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }


    private fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                }
                .onFailure { error ->
                }
        }
    }
}



data class ProfileDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)