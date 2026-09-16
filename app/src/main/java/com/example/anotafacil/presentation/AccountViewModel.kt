package com.example.anotafacil.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    fun testDeleteAccount(
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {

            val result = accountRepository.testDeleteAccount()

            result
                .onSuccess { uid ->
                    onResult("UID recebido pelo backend: $uid")
                }
                .onFailure { error ->
                    onResult(
                        "Erro: ${error.message}"
                    )
                }
        }
    }
}