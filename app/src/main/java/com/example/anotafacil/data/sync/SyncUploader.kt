package com.example.anotafacil.data.sync

import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.PaymentDao
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.domain.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class SyncUploader @Inject constructor(
    private val cityDao: CityDao,
    private val customerDao: CustomerDao,
    private val productDao: ProductDao,
    private val purchaseDao: PurchaseDao,
    private val paymentDao: PaymentDao,
    private val firestore: FirebaseFirestore
){
    suspend fun upload(ownerId: String): Boolean {
        var success = true

        val citiesSuccess = uploadCities(ownerId)
        if (!citiesSuccess) {
            success = false
        }

        val customersSuccess =
            if (citiesSuccess) {
                uploadCustomers(ownerId)
            } else {
                false
            }

        if (!customersSuccess) {
            success = false
        }

        val productsSuccess = uploadProducts(ownerId)
        if (!productsSuccess) {
            success = false
        }

        val purchasesSuccess =
            if (customersSuccess && productsSuccess) {
                uploadPurchases(ownerId)
            } else {
                false
            }

        if (!purchasesSuccess) {
            success = false
        }

        val paymentsSuccess =
            if (customersSuccess) {
                uploadPayments(ownerId)
            } else {
                false
            }

        if (!paymentsSuccess) {
            success = false
        }

        return success
    }


    private suspend fun uploadCities(ownerId: String): Boolean {
        val pendingCities = cityDao.getCitiesBySyncStatus(SyncStatus.PENDING)

        var success = true

        for (city in pendingCities) {
            try {
                firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("cities")
                    .document(city.id.toString())
                    .set(mapOf("name" to city.name))
                    .await()

                val updatedRows = cityDao.updateSyncStatus(
                    cityId = city.id,
                    status = SyncStatus.SYNCED
                )

                if (updatedRows != 1) success = false


            } catch (e: Exception) {
                success = false
            }
        }

        return success
    }


    private suspend fun uploadCustomers(ownerId: String): Boolean {
        val pendingCustomers = customerDao.getCustomersBySyncStatus(SyncStatus.PENDING)

        var success = true

        for (customer in pendingCustomers) {
            try {
                firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("customers")
                    .document(customer.id.toString())
                    .set(
                        mapOf(
                            "name" to customer.name,
                            "lastPurchaseDate" to customer.lastPurchaseDate,
                            "owes" to customer.owes,
                            "extraInfo" to customer.extraInfo,
                            "cityId" to customer.cityId.toString()
                        )
                    )
                    .await()

                val updatedRows = customerDao.updateSyncStatus(
                    customerId = customer.id,
                    status = SyncStatus.SYNCED
                )

                if (updatedRows != 1) success = false
            } catch (e: Exception) {
                success = false
            }
        }

        return success
    }


    private suspend fun uploadProducts(ownerId: String): Boolean {
        val pendingProducts = productDao.getProductsBySyncStatus(SyncStatus.PENDING)

        var success = true

        for (product in pendingProducts) {
            try {
                firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("products")
                    .document(product.id.toString())
                    .set(
                        mapOf(
                            "name" to product.name,
                            "price" to product.price
                        )
                    )
                    .await()

                val updatedRows = productDao.updateSyncStatus(
                    productId = product.id,
                    status = SyncStatus.SYNCED
                )

                if (updatedRows != 1) {
                    success = false
                }

            } catch (e: Exception) {
                success = false
            }
        }

        return success
    }


    private suspend fun uploadPurchases(ownerId: String): Boolean {
        val pendingPurchases = purchaseDao.getPurchasesBySyncStatus(SyncStatus.PENDING)

        var success = true

        for (purchaseData in pendingPurchases) {
            try {
                val purchase = purchaseData.purchase

                val items = purchaseData.items.map { itemData ->
                    mapOf(
                        "productId" to itemData.cartItem.productId.toString(),
                        "quantity" to itemData.cartItem.quantity,
                        "unitPrice" to itemData.cartItem.unitPrice,
                        "subtotal" to itemData.cartItem.subtotal
                    )
                }

                firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("purchases")
                    .document(purchase.id.toString())
                    .set(
                        mapOf(
                            "customerId" to purchase.customerId.toString(),
                            "purchaseDate" to purchase.purchaseDate,
                            "totalAmount" to purchase.totalAmount,
                            "items" to items
                        )
                    )
                    .await()

                val updatedRows = purchaseDao.updateSyncStatus(
                    purchaseId = purchase.id,
                    status = SyncStatus.SYNCED
                )

                if (updatedRows != 1) {
                    success = false
                }

            } catch (e: Exception) {
                success = false
            }
        }

        return success
    }


    private suspend fun uploadPayments(ownerId: String): Boolean {
        val pendingPayments = paymentDao.getPaymentsBySyncStatus(
            SyncStatus.PENDING
        )

        var success = true

        for (payment in pendingPayments) {
            try {
                firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("payments")
                    .document(payment.id.toString())
                    .set(
                        mapOf(
                            "customerId" to payment.customerId.toString(),
                            "paymentDate" to payment.paymentDate,
                            "amount" to payment.amount,
                            "isTotalPayment" to payment.isTotalPayment
                        )
                    )
                    .await()

                val updatedRows = paymentDao.updatePaymentSyncStatus(
                    paymentId = payment.id,
                    status = SyncStatus.SYNCED
                )

                if (updatedRows != 1) {
                    success = false
                }

            } catch (e: Exception) {
                success = false
            }
        }

        return success
    }
}