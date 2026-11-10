package com.example.anotafacil.data.billing


sealed interface PurchaseNextStep {
    data object AwaitingPayment : PurchaseNextStep

    data class VerifyOnServer(val purchaseToken: String) : PurchaseNextStep

    data object Ignore : PurchaseNextStep
}