package com.example.anotafacil.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.PurchasesResponseListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume


@Singleton
class BillingManager @Inject constructor(@ApplicationContext context: Context) {

    private val _purchaseUpdates = MutableSharedFlow<List<Purchase>>(
        extraBufferCapacity = 1
    )
    val purchaseUpdates = _purchaseUpdates.asSharedFlow()


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.let { _purchaseUpdates.tryEmit(it) }
            }
        }


    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()



    suspend fun connect(): Boolean {
        if (billingClient.isReady) return true

        return suspendCancellableCoroutine { continuation ->

            billingClient.startConnection(
                object : BillingClientStateListener {

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (continuation.isActive) {
                            continuation.resume(
                                billingResult.responseCode == BillingClient.BillingResponseCode.OK
                            )
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        // A reconexão automática está habilitada.
                    }
                }
            )
        }
    }



    suspend fun querySubscriptionProduct(
        productId: String
    ): ProductDetails? {

        if (!connect()) return null

        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        return suspendCancellableCoroutine { continuation ->

            billingClient.queryProductDetailsAsync(params) {billingResult, queryResult ->
                if (continuation.isActive) {
                    val productDetails =
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                            queryResult.productDetailsList.firstOrNull()
                        else
                            null

                    continuation.resume(productDetails)
                }
            }
        }
    }



    fun launchSubscriptionPurchase(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): BillingResult {

        val productParams =
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        return billingClient.launchBillingFlow(activity, params)
    }



    suspend fun queryActiveSubscriptions(): List<Purchase> {

        if (!connect()) return emptyList()

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        return suspendCancellableCoroutine { continuation ->

            billingClient.queryPurchasesAsync(
                params,
                PurchasesResponseListener { billingResult, purchases ->

                    if (continuation.isActive) {
                        continuation.resume(
                            if (
                                billingResult.responseCode ==
                                BillingClient.BillingResponseCode.OK
                            ) purchases else emptyList()
                        )
                    }
                }
            )
        }
    }
}
