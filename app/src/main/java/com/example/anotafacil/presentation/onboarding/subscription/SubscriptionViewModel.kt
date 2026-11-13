package com.example.anotafacil.presentation.onboarding.subscription

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.example.anotafacil.data.billing.BillingManager
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

    private var subscriptionProduct: ProductDetails? = null
    private var subscriptionOfferToken: String? = null

    init {
        loadSubscriptionProduct()
    }

    fun loadSubscriptionProduct() {
        viewModelScope.launch {
            _subscriptionState.update {
                it.copy(status = SubscriptionStatus.LOADING, errorMessage = null)
            }

            val productDetails = billingManager.querySubscriptionProduct("premium")
            if (productDetails == null) {
                subscriptionProduct = null
                subscriptionOfferToken = null
                _subscriptionState.update {
                    it.copy(
                        status = SubscriptionStatus.ERROR,
                        errorMessage = "Não foi possível carregar o plano. Tente novamente."
                    )
                }
                return@launch
            }

            val monthlyPlan = productDetails.subscriptionOfferDetails
                ?.firstOrNull { offer ->
                    offer.basePlanId == "mensal" && offer.offerId == null
                }
            val price = monthlyPlan
                ?.pricingPhases
                ?.pricingPhaseList
                ?.lastOrNull()
                ?.formattedPrice

            if (monthlyPlan == null || price.isNullOrBlank()) {
                subscriptionProduct = null
                subscriptionOfferToken = null
                _subscriptionState.update {
                    it.copy(
                        status = SubscriptionStatus.ERROR,
                        errorMessage = "O plano mensal não está disponível no Google Play."
                    )
                }
                return@launch
            }

            subscriptionProduct = productDetails
            subscriptionOfferToken = monthlyPlan.offerToken
            _subscriptionState.update {
                it.copy(
                    status = SubscriptionStatus.READY,
                    productName = productDetails.name,
                    productDescription = productDetails.description,
                    formattedMonthlyPrice = price
                )
            }
        }
    }

    fun testPremiumPurchase(activity: Activity) {
        viewModelScope.launch {
            val productDetails = subscriptionProduct ?: return@launch
            val offerToken = subscriptionOfferToken ?: return@launch

            val result = billingManager.launchSubscriptionPurchase(
                activity = activity,
                productDetails = productDetails,
                offerToken = offerToken
            ) ?: return@launch

            Log.d(
                "BillingTest",
                "Resultado da abertura: ${result.responseCode} - ${result.debugMessage}"
            )
        }
    }

    fun restoreSubscriptionPurchases() {
        billingManager.restoreSubscriptionPurchases()
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
    val errorMessage: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.LOADING,
    val productName: String? = null,
    val productDescription: String? = null,
    val formattedMonthlyPrice: String? = null,
)

enum class SubscriptionStatus {
    LOADING, READY, PROCESSING, PREMIUM, ERROR
}
