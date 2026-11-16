package com.example.anotafacil.domain.Response


sealed interface BillingManagerResponse {

    data class HasPremium(val uid: String) : BillingManagerResponse

    data class NoPremium(val uid: String) : BillingManagerResponse

    data object Disconnected : BillingManagerResponse

    data object NoDocument : BillingManagerResponse

    data object Unknown : BillingManagerResponse


    data class Error(val message: String?) : BillingManagerResponse
}