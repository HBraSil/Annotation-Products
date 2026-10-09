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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AccountProfileViewModel @Inject constructor(
    private val accountProfileRepository: AccountProfileRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState = _uiState.asStateFlow()


    init {
        getUserData()
    }


    fun getUserData() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess {
                    Log.d("AccountProfileViewModel", "Dados do usuário: $it")
                    _uiState.update { uiState ->
                        uiState.copy(
                            name = FieldState(field = it.name),
                            email = FieldState(field = it.email),
                            user = it
                        )
                    }
                }
                .onFailure { error ->
                    Log.d(
                        "AccountProfileViewModel",
                        "Falha ao buscar dados do usuário.: ${error.message}"
                    )
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
                println("Saving changes...: ${name.isValid}, ${email.isValid}")
                if (!name.isValid || !email.isValid) {
                    Log.d(
                        "AccountProfileViewModel",
                        "Formulário inválido. Não é possível salvar alterações."
                    )
                    return@launch
                }

                userRepository.saveChanges(
                    newUserName = if (wasNameChanged) user?.copy(name = name.field) else null,
                    newUserEmail = if (wasEmailChanged) user?.copy(email = email.field) else null
                )
                .onSuccess { text ->
                    _uiState.update {
                        it.copy(
                            wasNameChanged = false,
                            wasEmailChanged = false,
                            emailSentMessage = text
                        )
                    }
                }
                .onFailure { error ->
                    Log.d(
                        "AccountProfileViewModel",
                        "Falha ao salvar alterações.: ${error.message}"
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
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
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
    val user: User? = null,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val wasNameChanged: Boolean = false,
    val wasEmailChanged: Boolean = false,
    val successfullyDeleted: Boolean = false,
    val emailSentMessage: String? = null
)