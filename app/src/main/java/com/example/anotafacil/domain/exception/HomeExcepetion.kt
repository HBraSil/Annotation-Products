package com.example.anotafacil.domain.exception

import com.example.anotafacil.domain.model.SellerHomeUsers

// Exceção base da sua camada de domínio (opcional, mas boa prática)
sealed interface HomeResult {

    data class Success(val users: SellerHomeUsers) : HomeResult

    data class NotFound(val message: String? = null) : HomeResult

    data object Disconnected : HomeResult

    data object ErrorToParse : HomeResult

    data class Error(val message: String? = null) : HomeResult

    data object Connected : HomeResult

    data object NotConnected : HomeResult

    data object NotAuthenticated : HomeResult
}

sealed interface SellerConnectionState {

    data object NotConnected : SellerConnectionState

    data object Connected : SellerConnectionState

    data object Disconnected : SellerConnectionState

    data object NotFound : SellerConnectionState

    data class Error(
        val message: String?
    ) : SellerConnectionState
}