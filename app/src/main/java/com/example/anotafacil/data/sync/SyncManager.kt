package com.example.anotafacil.data.sync

import android.util.Log
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.domain.model.PurchaseWithItemsData
import com.example.anotafacil.domain.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val purchaseDao: PurchaseDao,
    private val firestore: FirebaseFirestore
) {

    suspend fun sync(): Boolean = syncPurchases()


    private suspend fun syncPurchases(): Boolean {

        Log.d("SyncManager", "Iniciando sincronização de compras")
        val purchases = purchaseDao.getPurchasesBySyncStatus(SyncStatus.PENDING)
        Log.d("SyncManager", "Depois de getPurchasesBySyncStatus")

        for (purchaseData in purchases) {

            try {
                syncPurchaseInFirestore(purchaseData)
                Log.d(
                    "SyncManager",
                    "Compra sincronizada: ${purchaseData.purchase.id}"
                )

                val result = purchaseDao.updateSyncStatus(
                    purchaseData.purchase.id,
                    SyncStatus.SYNCED
                )

                Log.d(
                    "SyncManager",
                    "Compra sincronizada: ${purchaseData.purchase.id}"

                )
                if (result <= 0) {
                    Log.e(
                        "SyncManager",
                        "Erro ao atualizar status da compra: $result"
                    )
                    return false
                }
            } catch (e: Exception) {

                Log.e(
                    "SyncManager",
                    "Erro ao sincronizar compra: ${purchaseData.purchase.id}",
                    e
                )
                return false
            }
        }

        return true
    }

    private suspend fun syncPurchaseInFirestore(
        purchaseData: PurchaseWithItemsData
    ) {

        val purchase = purchaseData.purchase

        val items = purchaseData.items.map { itemData ->

            mapOf(
                "productId" to itemData.cartItem.productId.toString(),
                "quantity" to itemData.cartItem.quantity,
                "unitPrice" to itemData.cartItem.unitPrice,
                "subtotal" to itemData.cartItem.subtotal
            )
        }

        val purchaseDataMap = mapOf(
            "customerId" to purchase.customerId.toString(),
            "purchaseDate" to purchase.purchaseDate,
            "totalAmount" to purchase.totalAmount,
            "items" to items
        )

        firestore
            .collection("owners")
            .document(purchase.ownerId)
            .collection("purchases")
            .document(purchase.id.toString())
            .set(purchaseDataMap)
            .await()
    }
}