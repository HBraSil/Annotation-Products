package com.example.anotafacil.domain.exception


sealed interface ConfirmEmailChangeResult {

    data object NoPendingChange :
        ConfirmEmailChangeResult

    data object Confirmed :
        ConfirmEmailChangeResult

    data object RequiresReauthentication :
        ConfirmEmailChangeResult
}