package com.example.anotafacil

sealed interface AccountStatus {
    data object Active : AccountStatus
    data object SellerDisconnected : AccountStatus
    data object OwnerDeleted : AccountStatus
    data object NotAuthenticated : AccountStatus
    data class Error(val message: String?) : AccountStatus
}