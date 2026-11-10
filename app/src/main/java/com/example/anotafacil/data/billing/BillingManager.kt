package com.example.anotafacil.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
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
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume


@Singleton
class BillingManager @Inject constructor(@ApplicationContext context: Context) {

    private val _purchaseUpdates = MutableSharedFlow<List<Purchase>>(
        extraBufferCapacity = 1
    )
    val purchaseUpdates = _purchaseUpdates.asSharedFlow()


    /** O purchasesUpdatedListener armazena um listener da biblioteca Google Play Billing,
     * responsável por receber notificações sobre o resultado de uma compra
     * ou mudanças relevantes no estado dela.
     *
     * * *billingResult*: informa o resultado da operação, como sucesso, cancelamento ou erro.
     * * *purchases*: contém os objetos Purchase relacionados à operação, incluindo informações como purchaseToken e purchaseState.*/
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Purchase.PurchaseState.PURCHASED
                purchases?.let { _purchaseUpdates.tryEmit(it) }
            }
        }


    /**
     * Configuração do BillingClient
     * */
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams
                .newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()


    /**
     * Conecta ao BillingClient
     *
     * Garante que o BillingClient consiga estabelecer uma conexão com o serviço de faturamento
     * do Google Play e informar se essa conexão foi estabelecida com sucesso.
     * */
    suspend fun connect(): Boolean {
        // Verifica se o BillingClient já está conectado
        if (billingClient.isReady) return true

        return suspendCancellableCoroutine { continuation ->

            billingClient.startConnection(
                object : BillingClientStateListener {

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        // continuation.isActive verifica se a corrotina ainda está ativa antes de tentar retomar a execução.
                        if (continuation.isActive) {
                            // BillingClient.BillingResponseCode.OK se refere ao sucesso da conexão, não à conclusão de um pagamento.
                            continuation.resume(
                                billingResult.responseCode == BillingClient.BillingResponseCode.OK
                            )
                        }
                    }

                    override fun onBillingServiceDisconnected() {/*A reconexão automática está habilitada.*/}
                }
            )
        }
    }



    // Busca por produtos de tipo SUBS criados no Play Console
    suspend fun querySubscriptionProduct(productId: String): ProductDetails? {

        if (!connect()) {
            Log.e("BillingTest", "Falha ao conectar com o Google Play")
            return null
        }

        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        return suspendCancellableCoroutine { continuation ->

            billingClient.queryProductDetailsAsync(params) {billingResult, queryResult ->

                Log.d(
                    "BillingTest",
                    "ResponseCode: ${billingResult.responseCode}"
                )

                Log.d(
                    "BillingTest",
                    "DebugMessage: ${billingResult.debugMessage}"
                )

                Log.d(
                    "BillingTest",
                    "Produtos encontrados: ${queryResult.productDetailsList.map { it.productId }}"
                )

                queryResult.unfetchedProductList.forEach { product ->
                    Log.e(
                        "BillingTest",
                        "Produto não recuperado: ${product.productId}, Status: ${product.statusCode}"
                    )
                }


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



    /**
     * Consulta o Google Play para recuperar
     * compras de assinaturas que o usuário ainda possui.
     * */
    suspend fun queryActiveSubscriptions(): List<Purchase> {

        if (!connect()) return emptyList()

        // Consulta compras do tipo assinatura que pertencem à conta do Google Play logada ‘smartphone’
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

    private suspend fun processPurchase(purchase: Purchase) {

        when (purchase.purchaseState) {
            Purchase.PurchaseState.PENDING -> {
                // Pagamento pendente.
                // Não liberar Premium.
            }

            Purchase.PurchaseState.PURCHASED -> {
                val purchaseToken = purchase.purchaseToken

                // Próxima etapa:
                // Enviar o token ao backend para validação.
            }

            else -> {
                // Compra sem estado concluído.
                // Não liberar Premium.
            }
        }
    }


    suspend fun testVerifySubscriptionConnection(): String {
        return try {
            FirebaseFunctions
                .getInstance("southamerica-east1")
                .getHttpsCallable("verifySubscription")
                .call(emptyMap<String, String>())
                .await()

            "Função respondeu com sucesso"

        } catch (e: FirebaseFunctionsException) {
            when (e.code) {
                FirebaseFunctionsException.Code.INVALID_ARGUMENT ->
                    "Conexão funcionando! A função recebeu nossa solicitação."

                FirebaseFunctionsException.Code.UNAUTHENTICATED ->
                    "O usuário não está autenticado no Firebase."

                else ->
                    "Erro na função: ${e.code}"
            }
        }
    }

}
