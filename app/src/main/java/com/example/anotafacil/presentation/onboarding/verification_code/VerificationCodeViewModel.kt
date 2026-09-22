package com.example.anotafacil.presentation.onboarding.verification_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.OwnerCodeRepository
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


    fun updateCode(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun verifyCode() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val code = _uiState.value.code


            ownerCodeRepository.verifyCode(code)
                .onSuccess { result ->
                    if (!result)
                        _uiState.update {
                            it.copy(errorMessage = "Você já é proprietário. Não é possível entrar como vendedor")
                        }
                    else
                        _uiState.update { it.copy(success = true) }

                }.onFailure { throwable ->
                    _uiState.update { it.copy(errorMessage = throwable.message) }
                }

            delay(400.milliseconds)
            _uiState.update { it.copy(errorMessage = null, isLoading = false) }
        }
    }
}

data class VerificationCodeUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
)