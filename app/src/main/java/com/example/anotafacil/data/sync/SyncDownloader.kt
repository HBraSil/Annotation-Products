package com.example.anotafacil.data.sync

import android.util.Log
import androidx.room.withTransaction
import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.PaymentDao
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.data.entity.CartItemEntity
import com.example.anotafacil.data.entity.CityEntity
import com.example.anotafacil.data.entity.CustomerEntity
import com.example.anotafacil.data.entity.PaymentEntity
import com.example.anotafacil.data.entity.ProductEntity
import com.example.anotafacil.data.entity.PurchaseEntity
import com.example.anotafacil.data.model.FirestorePurchase
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.domain.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlin.uuid.Uuid

class SyncDownloader @Inject constructor(
    private val cityDao: CityDao,
    private val customerDao: CustomerDao,
    private val productDao: ProductDao,
    private val purchaseDao: PurchaseDao,
    private val paymentDao: PaymentDao,
    private val firestore: FirebaseFirestore,
    private val appDatabase: AppDatabase
) {

    suspend fun downloadAll(ownerId: String): Boolean {

        val citiesSuccess = downloadCities(ownerId)
        if (!citiesSuccess) return false


        val productsSuccess = downloadProducts(ownerId)
        if (!productsSuccess) return false


        val cities = cityDao.getAll().first()

        for (city in cities) {
            val success = downloadCityData(
                ownerId = ownerId,
                cityId = city.id
            )

            if (!success) {
                return false
            }
        }

        return true
    }

    private suspend fun downloadCities(ownerId: String): Boolean {
        return try {
            val snapshot = firestore
                .collection("owners")
                .document(ownerId)
                .collection("cities")
                .get()
                .await()

            for (document in snapshot.documents) {
                val cityId = Uuid.parse(document.id)
                val name = document.getString("name") ?: continue

                Log.d("SyncManager", "Downloading city: $name")
                val localCity = cityDao.getCity(cityId)

                if (localCity == null) {
                    cityDao.addCity(
                        CityEntity(
                            id = cityId,
                            name = name,
                            syncStatus = SyncStatus.SYNCED
                        )
                    )
                } else {
                    cityDao.addCity(localCity.copy(name = name))
                }
            }

            true

        } catch (e: Exception) {
            false
        }
    }


    suspend fun downloadCityData(
        ownerId: String,
        cityId: Uuid
    ): Boolean {
        val customersSuccess = downloadCustomers(ownerId, cityId)
        if (!customersSuccess) return false

        val purchasesSuccess = downloadPurchases(ownerId, cityId)
        if (!purchasesSuccess) return false

        val paymentsSuccess = downloadPayments(ownerId, cityId)


        return paymentsSuccess
    }





    private suspend fun downloadCustomers(
        ownerId: String,
        cityId: Uuid
    ): Boolean {

        return try {
            val snapshot = firestore
                .collection("owners")
                .document(ownerId)
                .collection("customers")
                .whereEqualTo("cityId", cityId.toString())
                .get()
                .await()

            for (document in snapshot.documents) {

                val customerId = Uuid.parse(document.id)

                val name = document.getString("name")
                    ?: continue

                val lastPurchaseDate =
                    document.getString("lastPurchaseDate")

                val owes =
                    document.getDouble("owes")

                val extraInfo =
                    document.getString("extraInfo")

                val localCustomer =
                    customerDao.getCustomer(customerId).first()

                when {
                    localCustomer == null -> {
                        customerDao.saveCustomer(
                            CustomerEntity(
                                id = customerId,
                                name = name,
                                lastPurchaseDate = lastPurchaseDate,
                                owes = owes,
                                extraInfo = extraInfo,
                                cityId = cityId,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                    }

                    localCustomer.syncStatus == SyncStatus.SYNCED -> {
                        customerDao.saveCustomer(
                            localCustomer.copy(
                                name = name,
                                lastPurchaseDate = lastPurchaseDate,
                                owes = owes,
                                extraInfo = extraInfo,
                                cityId = cityId,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                    }

                    localCustomer.syncStatus == SyncStatus.PENDING -> {
                        // Não sobrescreve alteração local pendente.
                    }
                }
            }

            true

        } catch (e: Exception) {
            false
        }
    }




    private suspend fun downloadPurchases(
        ownerId: String,
        cityId: Uuid
    ): Boolean {

        return try {

            val customers = customerDao.getCustomersByCity(cityId)

            for (customer in customers) {

                val snapshot = firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("purchases")
                    .whereEqualTo("customerId", customer.id.toString())
                    .get()
                    .await()


                for (document in snapshot.documents) {

                    val purchase = document.toObject(
                        FirestorePurchase::class.java
                    ) ?: continue

                    val purchaseId = Uuid.parse(document.id)

                    val localPurchase = purchaseDao.getPurchaseById(purchaseId)

                    if (
                        localPurchase != null &&
                        localPurchase.syncStatus == SyncStatus.PENDING
                    ) {
                        continue
                    }

                    val purchaseEntity = PurchaseEntity(
                        id = purchaseId,
                        customerId = Uuid.parse(purchase.customerId),
                        ownerId = ownerId,
                        purchaseDate = purchase.purchaseDate,
                        totalAmount = purchase.totalAmount,
                        syncStatus = SyncStatus.SYNCED
                    )

                    val cartItems = purchase.items.map {
                        CartItemEntity(
                            purchaseId = purchaseId,
                            productId = Uuid.parse(it.productId),
                            quantity = it.quantity,
                            unitPrice = it.unitPrice,
                            subtotal = it.subtotal
                        )
                    }


                    appDatabase.withTransaction {
                        purchaseDao.addPurchase(purchaseEntity)

                        if (cartItems.isNotEmpty()) {
                            customerDao.saveCartItems(cartItems)
                        }
                    }
                }
            }

            true

        } catch (e: Exception) {
            false
        }
    }


    private suspend fun downloadPayments(
        ownerId: String,
        cityId: Uuid
    ): Boolean {

        return try {

            val customers = customerDao.getCustomersByCity(cityId)

            for (customer in customers) {

                val snapshot = firestore
                    .collection("owners")
                    .document(ownerId)
                    .collection("payments")
                    .whereEqualTo("customerId", customer.id.toString())
                    .get()
                    .await()

                for (document in snapshot.documents) {

                    val paymentId = Uuid.parse(document.id)

                    val payment = PaymentEntity(
                        id = paymentId,
                        customerId = Uuid.parse(
                            document.getString("customerId") ?: continue
                        ),
                        paymentDate = document.getLong("paymentDate") ?: continue,
                        amount = document.getDouble("amount") ?: continue,
                        isTotalPayment = document.getBoolean("isTotalPayment") ?: continue,
                        syncStatus = SyncStatus.SYNCED
                    )

                    val localPayment =
                        paymentDao.getPaymentById(paymentId)

                    if (
                        localPayment != null &&
                        localPayment.syncStatus == SyncStatus.PENDING
                    ) {
                        continue
                    }

                    paymentDao.insertPayment(payment)
                }
            }

            true

        } catch (e: Exception) {
            false
        }
    }




    private suspend fun downloadProducts(
        ownerId: String
    ): Boolean {

        return try {

            val snapshot = firestore
                .collection("owners")
                .document(ownerId)
                .collection("products")
                .get()
                .await()

            for (document in snapshot.documents) {

                val productId = Uuid.parse(document.id)

                val name = document.getString("name") ?: continue

                val price = document.getLong("price")
                    ?.toInt()
                    ?: continue

                val localProduct =
                    productDao.getById(productId)

                when {
                    localProduct == null -> {
                        productDao.insertAll(
                            listOf(
                                ProductEntity(
                                    id = productId,
                                    name = name,
                                    price = price,
                                    syncStatus = SyncStatus.SYNCED
                                )
                            )
                        )
                    }

                    localProduct.syncStatus == SyncStatus.SYNCED -> {
                        productDao.updateProductPrice(
                            localProduct.copy(
                                name = name,
                                price = price,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                    }

                    localProduct.syncStatus == SyncStatus.PENDING -> {
                        // Não sobrescreve alteração local pendente.
                    }
                }
            }

            true

        } catch (e: Exception) {
            false
        }
    }

}