package com.example.anotafacil.presentation.onboarding.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase
import com.example.anotafacil.data.billing.BillingManager
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billingManager: BillingManager
): ViewModel() {

    private val _subscriptionState = MutableStateFlow(SubscriptionState())
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()


    init {
        viewModelScope.launch {


            testPremiumProduct()
           /* billingManager.purchaseUpdates.collect { purchases ->
                purchases.forEach { purchase ->
                    //processPurchase(purchase.purchaseToken)
                }
            }*/
        }
    }


    fun testPremiumProduct() {
        viewModelScope.launch {
            val productDetails = billingManager.querySubscriptionProduct(
                productId = "premium"
            )

            if (productDetails == null) {
                Log.e("BillingTest", "Produto premium não encontrado")
                return@launch
            }

            Log.d("BillingTest", "Produto: ${productDetails.productId}")

            val monthlyPlan = productDetails.subscriptionOfferDetails
                ?.firstOrNull { offer ->
                    offer.basePlanId == "mensal" && offer.offerId == null
                }

            if (monthlyPlan == null) {
                Log.e("BillingTest", "Plano mensal não encontrado")
                return@launch
            }

            val price = monthlyPlan.pricingPhases
                .pricingPhaseList
                .lastOrNull()
                ?.formattedPrice

            Log.d("BillingTest", "Plano: ${monthlyPlan.basePlanId}")
            Log.d("BillingTest", "Preço: $price")
            Log.d("BillingTest", "OfferToken disponível: ${monthlyPlan.offerToken.isNotBlank()}")
        }
    }

    fun testGooglePlaySubscription() {
        viewModelScope.launch {
            val purchases = billingManager.queryActiveSubscriptions()

            val purchase = purchases.firstOrNull {
                it.purchaseState == Purchase.PurchaseState.PURCHASED
            }

            if (purchase == null) {
                Log.d(
                    "BillingTest",
                    "Nenhuma assinatura comprada foi encontrada."
                )
                return@launch
            }

            try {
                val result = FirebaseFunctions
                    .getInstance("southamerica-east1")
                    .getHttpsCallable("verifySubscription")
                    .call(
                        mapOf(
                            "purchaseToken" to purchase.purchaseToken
                        )
                    )
                    .await()

                Log.d(
                    "BillingTest",
                    "Resposta do Google Play: ${result.data}"
                )

            } catch (e: FirebaseFunctionsException) {
                Log.e(
                    "BillingTest",
                    "Erro na Cloud Function: ${e.code} - ${e.message}"
                )
            } catch (e: Exception) {
                Log.e("BillingTest", "Erro inesperado", e)
            }
        }
    }



    fun finalizeSubscription() {
        _subscriptionState.value = SubscriptionState(isLoading = true)

        viewModelScope.launch {
            userRepository.becomeOwner()
                .onSuccess {
                    // Handle success
                    _subscriptionState.update { it.copy(isLoading = false, success = true) }
                    Log.d("SubscriptionViewModel", "Subscription finalized")
                }
                .onFailure {throwable ->
                    // Handle failure
                    _subscriptionState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                    Log.e("SubscriptionViewModel", "Error finalizing subscription: ${throwable.message}")
                }
        }
    }
}


data class SubscriptionState(
    val success: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)