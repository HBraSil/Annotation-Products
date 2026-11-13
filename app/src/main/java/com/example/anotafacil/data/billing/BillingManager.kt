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
import com.example.anotafacil.domain.Response.BillingManagerResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.snapshots
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume


@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseFunctions: FirebaseFunctions
) {

    private val observeCurrentUserUid: Flow<String?> =
        callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                trySend(firebaseAuth.currentUser?.uid)
            }

            auth.addAuthStateListener(listener)

            awaitClose {
                auth.removeAuthStateListener(listener)
            }
        }.distinctUntilChanged()



    private val _purchaseUpdates = MutableSharedFlow<List<Purchase>>(
        extraBufferCapacity = 1
    )
    val purchaseUpdates = _purchaseUpdates.asSharedFlow()

    private val billingScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private val verificationMutex = Mutex()


    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeCurrentSubscriptionState(): Flow<BillingManagerResponse> {
        return observeCurrentUserUid.flatMapLatest { uid ->
            if (uid == null) return@flatMapLatest flowOf(BillingManagerResponse.Disconnected)
            firestore
                .collection("subscriptionEntitlements")
                .document(uid)
                .snapshots(MetadataChanges.INCLUDE)
                .map { snapshot ->
                    if (snapshot.metadata.isFromCache) {
                        return@map BillingManagerResponse.Unknown
                    }
                    if (!snapshot.exists()) return@map BillingManagerResponse.NoDocument

                    when (snapshot.getBoolean("hasPremium")) {
                        true -> BillingManagerResponse.HasPremium
                        false -> BillingManagerResponse.NoPremium

                        null -> BillingManagerResponse.Error("O documento não possui o campo hasPremium.")
                    }
                }
                .onStart {
                    emit(BillingManagerResponse.Unknown)
                }
                .catch {
                    emit(
                        BillingManagerResponse.Error(it.message)
                    )
                }

        }
    }



    /** O purchasesUpdatedListener armazena um listener da biblioteca Google Play Billing,
     * responsável por receber notificações sobre o resultado de uma compra
     * ou mudanças relevantes no estado dela.
     *
     * * *billingResult*: informa o resultado da operação, como sucesso, cancelamento ou erro.
     * * *purchases*: contém os objetos Purchase relacionados à operação, incluindo informações como purchaseToken e purchaseState.*/
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->

            Log.d(
                "BillingTest",
                "Compra: ${billingResult.responseCode} - " +
                        billingResult.debugMessage
            )

            if (
                billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {
                val receivedPurchases = purchases.orEmpty()

                // O processamento não depende de um coletor do SharedFlow.
                billingScope.launch {
                    receivedPurchases.forEach { purchase ->
                        processPurchase(purchase)
                    }
                }

                // Continua disponível para quem observa os eventos.
                _purchaseUpdates.tryEmit(receivedPurchases)
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



    /**
     * Busca por produtos, especificamente, de tipo SUBS (BillingClient.ProductType.SUBS) criados no Play Console
     * */
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
    ): BillingResult? {
        val uid = auth.currentUser?.uid ?: return null

        val productParams = BillingFlowParams.ProductDetailsParams
            .newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .setObfuscatedAccountId(
                obfuscateAccountId(uid)
            )
            .build()

        return billingClient.launchBillingFlow(
            activity,
            params
        )
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
//                                      9/8
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
        //if ("premium" !in purchase.products) return

        if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Log.d("BillingTest", "Pagamento pendente.")
            return
        }

        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            return
        }

        verificationMutex.withLock {
            val uid = auth.currentUser?.uid

            if (uid == null) {
                Log.w(
                    "BillingTest",
                    "Verificação adiada: usuário não autenticado."
                )
                return@withLock
            }

            // Evita enviar compras de outra conta do app.
            // A validação definitiva continua no backend.
            val accountId = purchase.accountIdentifiers?.obfuscatedAccountId

            if (accountId != obfuscateAccountId(uid)) {
                Log.w(
                    "BillingTest",
                    "Compra não associada à conta atual."
                )
                return@withLock
            }

            try {
                Log.d(
                    "BillingTest",
                    "Enviando compra ao backend para validar e registrar."
                )

                firebaseFunctions
                    .getHttpsCallable("verifySubscription")
                    .call(
                        mapOf(
                            "purchaseToken" to purchase.purchaseToken
                        )
                    )
                    .await()

                Log.d(
                    "BillingTest",
                    "Validação e registro concluídos pelo backend."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: FirebaseFunctionsException) {
                Log.e(
                    "BillingTest",
                    "Falha na verificação: ${e.code} - ${e.message}"
                )
            } catch (e: Exception) {
                Log.e(
                    "BillingTest",
                    "Falha ao comunicar com o backend.",
                    e
                )
            }
        }
    }


    fun restoreSubscriptionPurchases() {
        billingScope.launch {
            try {
                if (auth.currentUser == null) return@launch

                val purchases = queryActiveSubscriptions()

                purchases.forEach { purchase ->
                    processPurchase(purchase)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(
                    "BillingTest",
                    "Falha ao recuperar assinaturas.",
                    e
                )
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

    private fun obfuscateAccountId(uid: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(uid.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { byte ->
            "%02x".format(byte.toInt() and 0xff)
        }
    }
}
