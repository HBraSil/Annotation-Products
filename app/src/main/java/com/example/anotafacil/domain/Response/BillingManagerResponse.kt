package com.example.anotafacil.domain.Response


sealed interface BillingManagerResponse {

    data object HasPremium : BillingManagerResponse

    data object NoPremium : BillingManagerResponse

    data object Disconnected : BillingManagerResponse

    data object NoDocument : BillingManagerResponse

    data object Unknown : BillingManagerResponse


    data class Error(val message: String?) : BillingManagerResponse
}