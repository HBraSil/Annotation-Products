package com.example.anotafacil.domain.exception

import com.example.anotafacil.domain.model.SellerHomeUsers

// Exceção base da sua camada de domínio (opcional, mas boa prática)
sealed interface HomeResult {

    data class Success(val users: SellerHomeUsers) : HomeResult

    data object NotFound : HomeResult

    data object Disconnected : HomeResult

    data object OwnerNotFound : HomeResult

    data class Error(val message: String? = null) : HomeResult

    data object NetworkError : HomeResult

    data object PermissionError : HomeResult
}
