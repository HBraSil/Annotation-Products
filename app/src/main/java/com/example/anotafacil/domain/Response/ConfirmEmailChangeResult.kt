package com.example.anotafacil.domain.Response


sealed interface ConfirmEmailChangeResult {

    data object NoPendingChange :
        ConfirmEmailChangeResult

    data object Confirmed :
        ConfirmEmailChangeResult

    data object RequiresReauthentication :
        ConfirmEmailChangeResult
}