package com.example.anotafacil.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.WhichScreenToGo
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(LoadingState())
    val uiState = _uiState.asStateFlow()

    init { initialDestination() }

    fun initialDestination() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            userRepository.resolveInitialDestination()
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            goTo = it
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = it.errorMessage
                        )
                    }
                }
        }
    }


    fun cancelLoading() = _uiState.update { it.copy(isLoading = false) }

}

data class LoadingState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val goTo: WhichScreenToGo? = null
)