package com.example.anotafacil.domain.Response

import com.example.anotafacil.domain.model.SellerHomeUsers

// Exceção base da camada de domínio
sealed interface HomeResponse {

    data class Success(val users: SellerHomeUsers) : HomeResponse

    data class NotFound(val message: String? = null) : HomeResponse

    data object Disconnected : HomeResponse

    data object ErrorToParse : HomeResponse

    data class Error(val message: String? = null) : HomeResponse

    data object CancellationException : HomeResponse

    data object NotConnected : HomeResponse

    data object NotAuthenticated : HomeResponse
}



sealed interface SellerConnectionState {

    data object NotConnected : SellerConnectionState

    data object Connected : SellerConnectionState

    data object Disconnected : SellerConnectionState
    data class Error(
        val message: String?
    ) : SellerConnectionState
}