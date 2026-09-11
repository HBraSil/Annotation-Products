package com.example.anotafacil.presentation.profile.manage_sellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.OwnerCode
import com.example.anotafacil.domain.repository.OwnerCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ManageSellersViewModel @Inject constructor(
    private val ownerCodeRepository: OwnerCodeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageSellersUiState())
    val uiState: StateFlow<ManageSellersUiState> = _uiState.asStateFlow()
    private var countdownJob: Job? = null


    init {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            ownerCodeRepository.getActiveCode()
                .onSuccess { code ->
                    println(
                         code.ownerId
                    )
                    _uiState.update { it.copy(ownerCode = code, isLoading = false) }

                    startCountdown(code.expiresAt)
                }
                .onFailure { throwable ->
                    println("Failed to retrieve active code.: ${throwable.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun generateOwnerCode() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            ownerCodeRepository.generateCode()
                .onSuccess { code ->
                    _uiState.update { it.copy(ownerCode = code, isLoading = false) }

                    startCountdown(code.expiresAt)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Não foi possível gerar o código."
                        )
                    }

                }

            delay(400.milliseconds)
            _uiState.update { it.copy(error = null) }
        }
    }



    private fun startCountdown(expiresAt: Long) {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (true) {
                val remaining = expiresAt - System.currentTimeMillis()

                if (remaining <= 0L) {
                    _uiState.update {
                        it.copy(
                            remainingTime = "00:00",
                            isCodeExpired = true
                        )
                    }
                    break
                }

                val totalSeconds = remaining / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60

                val remaningTime = "%02d:%02d".format(minutes, seconds)

                _uiState.update {
                    it.copy(remainingTime = remaningTime)
                }

                delay(1_000.milliseconds)
            }
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}

data class ManageSellersUiState(
    val ownerCode: OwnerCode = OwnerCode(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val remainingTime: String = "00:00",
    val isCodeExpired: Boolean = false,
)