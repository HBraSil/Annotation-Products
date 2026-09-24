package com.example.anotafacil.presentation.profile.account_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.AccountProfileRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.presentation.auth.FieldState
import com.example.anotafacil.ui.validation.EmailValidator
import com.example.anotafacil.ui.validation.NameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AccountProfileViewModel @Inject constructor(
    private val accountProfileRepository: AccountProfileRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState = _uiState.asStateFlow()


    fun updateName(name: String) {
        val isNameValid = NameValidator.isNameValid(name)

        _uiState.update {
            it.copy(
                name = FieldState(
                    field = name,
                    fieldError = isNameValid,
                    isValid = isNameValid == null
                )
            )
        }
    }


    fun updateEmail(email: String) {
        val isNameValid = EmailValidator.validate(email)

        _uiState.update {
            it.copy(
                name = FieldState(
                    field = email,
                    fieldError = isNameValid,
                    isValid = isNameValid == null
                )
            )
        }
    }


    fun showAccountDeletionConfirmationDialog() {
        _uiState.update {
            it.copy(showAccountDeletionConfirmationDialog = true)
        }
    }
    
    fun hideAccountDeletionConfirmationDialog() {
        _uiState.update {
            it.copy(showAccountDeletionConfirmationDialog = false)
        }
    }


    fun deleteAccount() {
        _uiState.update { it.copy(isDeleting = true) }

        viewModelScope.launch {
            delay(3.seconds)
            _uiState.update { it.copy(isDeleting = false, successfullyDeleted = true) }
            /*val result = accountProfileRepository.deleteAccount()

            result
                .onSuccess {
                    signOut()
                }
                .onFailure { error ->
                    Log.d("AccountProfileViewModel", "Falha ao deletar a conta.")
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            error = error.message
                        )
                    }
                }*/
        }
    }


    private fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            successfullyDeleted = true
                        )
                    }
                }
                .onFailure { error ->
                    Log.d("AccountProfileViewModel", "Falha ao fazer logout.: ${error.message}")
                }
        }
    }
}



data class ProfileDetailUiState(
    val name: FieldState = FieldState(),
    val email: FieldState = FieldState(),
    val isDeleting: Boolean = false,
    val error: String? = null,
    val successfullyDeleted: Boolean = false,
    val showAccountDeletionConfirmationDialog: Boolean = false
)