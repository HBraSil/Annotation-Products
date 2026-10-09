package com.example.anotafacil.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.domain.usecase.UploadDataUseCase
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val uploadDataUseCase: UploadDataUseCase,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()


    init {
        getOwner()
        updateHasInternetConnection()
    }


    private fun updateHasInternetConnection() {
        viewModelScope.launch {
            networkChecker.isOnline.collect { isOnline ->
                _uiState.update {
                    it.copy(
                        hasInternetConnection = if (isOnline) "Conexão com internet" else null
                    )
                }
            }
        }
    }


    private fun getOwner() {
        viewModelScope.launch {
            userRepository.observeOwnerInfo().collect { result ->
                when (result) {
                    is HomeResult.Success -> {
                        _uiState.update { it.copy(user = result.users.owner) }
                    }
                    is HomeResult.NotFound -> {
                        _uiState.update { it.copy(isUserDisconnected = "Proprietário não encontrado") }
                    }
                    is HomeResult.Disconnected -> {
                        _uiState.update { it.copy(message = "Você foi desconectado do proprietário") }
                    }
                    is HomeResult.Error -> {
                        _uiState.update { it.copy(message = result.message) }
                    }
                    else -> {}
                }
            }
        }
    }


    fun syncCloudClick() {
        if (_uiState.value.hasInternetConnection == null) return

        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            uploadDataUseCase()
                .onSuccess {
                    _uiState.update {
                        it.copy(message = "Dados sincronizados com sucesso!")
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(message = throwable.message)
                    }
                }

            delay(400.milliseconds)
            _uiState.update { it.copy(isSyncing = false, message = null) }
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
    val message: String? = null,
    val user: User? = null,
    val hasInternetConnection: String? = null,
    val isUserDisconnected: String? = null
)