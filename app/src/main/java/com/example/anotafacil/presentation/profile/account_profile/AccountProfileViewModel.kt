package com.example.anotafacil.presentation.profile.account_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.AccountProfileRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.presentation.auth.FieldState
import com.example.anotafacil.ui.validation.EmailValidator
import com.example.anotafacil.ui.validation.NameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AccountProfileViewModel @Inject constructor(
    private val accountProfileRepository: AccountProfileRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        getUserInfo()
    }


    fun getUserInfo() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect {
                it.onSuccess { user ->
                    Log.d("AccountProfileViewModel", "Dados do usuário: $user")
                    _uiState.update { state ->
                        state.copy(
                            name = FieldState(field = user.name),
                            email = FieldState(field = user.email),
                            user = user
                        )
                    }
                }
                    .onFailure { throwable ->
                        Log.e("AccountProfileViewModel", "ERRO: ${throwable.message}")
                        _uiEvent.emit(
                            ProfileDetailUiEvent.UiMessage(
                                message = throwable.message
                            )
                    )
                }
            }
        }
    }


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

        wasFormChanged(name = name)
    }


    fun updateEmail(email: String) {
        val isEmailValid = EmailValidator.validate(email)

        _uiState.update {
            it.copy(
                email = FieldState(
                    field = email,
                    fieldError = isEmailValid,
                    isValid = isEmailValid == null
                )
            )
        }

        wasFormChanged(email = email)
    }


    fun wasFormChanged(name: String? = null, email: String? = null) {
        val currentName = name ?: _uiState.value.name.field
        val currentEmail = email ?: _uiState.value.email.field

        val nameChanged = currentName != _uiState.value.user?.name
        val emailChanged = currentEmail != _uiState.value.user?.email

        _uiState.update {
            it.copy(
                wasNameChanged = nameChanged,
                wasEmailChanged = emailChanged
            )
        }
    }


    fun saveChanges() {
        viewModelScope.launch {
            with(_uiState.value) {

                userRepository.saveChanges(
                    newUserName = if (wasNameChanged) user?.copy(name = name.field) else null,
                    newUserEmail = if (wasEmailChanged) user?.copy(email = email.field) else null
                )
                    .onSuccess { text ->
                        _uiEvent.emit(
                            ProfileDetailUiEvent.UiMessage(message = text)
                        )
                        _uiState.update {
                            it.copy(
                                wasNameChanged = false,
                                wasEmailChanged = false,
                            )
                        }
                    }
                    .onFailure { throwable ->
                        _uiEvent.emit(
                            ProfileDetailUiEvent.UiMessage(
                                message = throwable.message
                            )
                        )
                    }

            }
        }
    }

    fun deleteAccount() {
        _uiState.update { it.copy(isDeleting = true) }

        viewModelScope.launch {
            val result = accountProfileRepository.deleteAccount()

            result
                .onSuccess {
                    signOut()
                }
                .onFailure { error ->
                    _uiEvent.emit(
                        ProfileDetailUiEvent.UiMessage(
                            message = error.message
                        )
                    )
                }

            _uiState.update { it.copy(isDeleting = false) }
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
                    _uiEvent.emit(
                        ProfileDetailUiEvent.UiMessage(
                            message = error.message ?: "Erro ao sair"
                        )
                    )
                }
        }
    }
}


data class ProfileDetailUiState(
    val name: FieldState = FieldState(),
    val email: FieldState = FieldState(),
    val user: User? = null,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val wasNameChanged: Boolean = false,
    val wasEmailChanged: Boolean = false,
    val successfullyDeleted: Boolean = false,
)

sealed interface ProfileDetailUiEvent {
    data class UiMessage(val message: String?) : ProfileDetailUiEvent
}