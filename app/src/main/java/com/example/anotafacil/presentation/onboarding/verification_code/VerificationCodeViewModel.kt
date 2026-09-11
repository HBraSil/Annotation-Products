package com.example.anotafacil.presentation.onboarding.verification_code

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.OwnerCodeRepository
import com.example.anotafacil.presentation.auth.FieldState
import com.example.anotafacil.ui.validation.NameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val ownerCodeRepository: OwnerCodeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationCodeUiState())
    val uiState: StateFlow<VerificationCodeUiState> = _uiState.asStateFlow()


    fun updateSellerName(name: String) {
        val isNameValid = NameValidator.isNameValid(name)

        _uiState.update {
            it.copy(sellerName = FieldState(
                field = name,
                fieldError = isNameValid,
                isValid = isNameValid == null
            ))
        }
    }

    fun updateCode(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun verifyCode() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val code = _uiState.value.code
            val name = _uiState.value.sellerName.field


            ownerCodeRepository.verifyCode(code, name)
                .onSuccess { result ->
                    Log.d("VerificationCodeViewModel", "Code verified successfully: $result")
                    if (!result) {
                        _uiState.update { it.copy(errorMessage = "Usuário já é proprietário. Não é possível entrar como vendedor") }
                    } else {
                        _uiState.update { it.copy(success = true) }
                    }
                }.onFailure { throwable ->
                    Log.e("VerificationCodeViewModel", "Error verifying code: ${throwable.message}")
                    _uiState.update { it.copy(errorMessage = throwable.message) }
                }

            delay(400.milliseconds)
            _uiState.update { it.copy(errorMessage = null, isLoading = false) }
        }
    }
}

data class VerificationCodeUiState(
    val code: String = "",
    val sellerName: FieldState = FieldState(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
)