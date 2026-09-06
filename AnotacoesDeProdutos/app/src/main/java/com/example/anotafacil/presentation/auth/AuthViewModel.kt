package com.example.anotafacil.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.AuthRepository
import com.example.anotafacil.validation.EmailValidator
import com.example.anotafacil.validation.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun updateEmail(email: String) {
        val isEmailValid = EmailValidator.validate(email)

        _uiState.update {
            it.copy(
                email = FieldState(
                    field = email,
                    fieldError = isEmailValid,
                    isValid = isEmailValid == null,
                )
            )
        }

        updateLoginButton()
    }

    fun updatePassword(password: String) {
        val isPasswordValid = PasswordValidator.isValidPassword(password)

        _uiState.update {
            it.copy(
                password = FieldState(
                    field = password,
                    fieldError = isPasswordValid,
                    isValid = isPasswordValid == null,
                )
            )
        }

        updateLoginButton()
    }

    fun updateLoginButton() {
        with(_uiState.value) {
            _uiState.update {
                it.copy(
                    allFieldsValid = email.isValid && password.isValid
                )
            }
        }
    }

    fun loginWithEmailAndPassword() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val email = _uiState.value.email.field
            val password = _uiState.value.password.field

            authRepository.loginWithEmailAndPassword(email, password)
                .onSuccess {
                    _uiState.update { it.copy(success = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(error = it.error) }
                }

        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val allFieldsValid: Boolean = false,
)

data class FieldState(
    val field: String = "",
    val fieldError: String? = null,
    val isValid: Boolean = false,
)
