package com.example.anotafacil.data.billing

import com.android.billingclient.api.Purchase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.tasks.await


class SubscriptionPurchaseProcessor {

    fun determineNextStep(purchase: Purchase): PurchaseNextStep {
        return when (purchase.purchaseState) {

            Purchase.PurchaseState.PENDING -> {
                PurchaseNextStep.AwaitingPayment
            }

            Purchase.PurchaseState.PURCHASED -> {
                PurchaseNextStep.VerifyOnServer(
                    purchaseToken = purchase.purchaseToken
                )
            }

            else -> {
                PurchaseNextStep.Ignore
            }
        }
    }


}
