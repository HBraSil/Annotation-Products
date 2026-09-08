package com.example.anotafacil.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.AuthRepository
import com.example.anotafacil.ui.validation.EmailValidator
import com.example.anotafacil.ui.validation.NameValidator
import com.example.anotafacil.ui.validation.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun updateName(name: String) {
        val isNameValid = NameValidator.isNameValid(name)


        _uiState.update {
            it.copy(
                name = FieldState(
                    field = name,
                    fieldError = isNameValid,
                    isValid = isNameValid == null,
                )
            )
        }

        updateLoginButton()
    }

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
                    loginFieldsValid = email.isValid && password.isValid,
                    signUpFieldsValid = email.isValid && password.isValid && name.isValid
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
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message) }
                }

        }
    }

    fun loginWithGoogle() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.loginWithGoogle()
                .onSuccess {
                    _uiState.update { it.copy(success = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message) }
                }
        }
    }

    fun signUpWithEmailAndPassword() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val email = _uiState.value.email.field
            val password = _uiState.value.password.field
            val name = _uiState.value.name.field

            authRepository.signUpWithEmailAndPassword(name, email, password)
                .onSuccess { result ->
                    _uiState.update { it.copy(success = result) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message) }
                }

            delay(500.milliseconds)
            _uiState.update { it.copy(error = null, isLoading = false) }
        }
    }
}

data class AuthUiState(
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val name: FieldState = FieldState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val loginFieldsValid: Boolean = false,
    val signUpFieldsValid: Boolean = false,
)

data class FieldState(
    val field: String = "",
    val fieldError: String? = null,
    val isValid: Boolean = false,
)
