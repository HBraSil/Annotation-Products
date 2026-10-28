package com.example.anotafacil.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.PaymentDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.data.entity.toDomain
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import com.example.anotafacil.domain.model.SyncStatus
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toCartEntity
import com.example.anotafacil.domain.model.toCustomerEntity
import com.example.anotafacil.domain.model.toDomain
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.Uuid

class CustomerRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val appDatabase: AppDatabase,
    private val customerDao: CustomerDao,
    private val purchaseDao: PurchaseDao,
    private val paymentDao: PaymentDao,
    private val userRepository: UserRepository,
) : CustomerRepository {
    override fun getCustomer(id: Uuid?): Flow<Customer?> {
        return customerDao.getCustomer(id).map {
            Log.d("CustomerRepositoryImpl", "getCustomer: $it")
            it?.toDomain() }
    }

    override fun getAllCustomers(cityId: Uuid?): Flow<Result<List<Customer>>> {

        return try {
            customerDao.getAll(cityId).map { customerList ->
                Result.success(customerList.map { it.toDomain() })
            }
        } catch (e: Exception) {
            flowOf(Result.failure(Exception("Error ao carregar clientes")))
        }
    }

    override suspend fun addCustomer(customer: Customer): Result<Boolean> {
        return try {
            customerDao.saveCustomer(customer.toCustomerEntity())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Error ao adicionar cliente"))
        }
    }

    override suspend fun newPurchase(purchase: Purchase): Result<Unit> {
        return try {
            purchaseDao.addPurchase(purchase.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error ao adicionar compra"))
        }
    }

    override suspend fun updateCustomer(customer: Customer): Int {
        return customerDao.updateCustomer(customer.toCustomerEntity())

    }

    override suspend fun payOffTotalDebt(
        customer: Customer,
        payment: Payment,
    ): Pair<Int, Long> {
        return Pair(
            customerDao.updateCustomer(customer.toCustomerEntity()),
            paymentDao.insertPayment(payment.toEntity())
        )
    }

    override suspend fun saveCartItems(cartItems: List<CartItem>): List<Long> {
        return customerDao.saveCartItems(cartItems.map { it.toCartEntity() })
    }


    override suspend fun deleteCustomer(customerId: Uuid?): Result<Unit> {
        return try {
            val id = customerId
                ?: return Result.failure(
                    IllegalArgumentException("ID do cliente inválido")
                )

            val firebaseUid = auth.currentUser?.uid
                ?: return Result.failure(
                    Exception("Usuário não autenticado")
                )

            val user = userRepository.getCurrentUser(firebaseUid)
                .getOrElse { return Result.failure(it) }

            Log.d("CustomerRepositoryImpl", "deleteCustomer: $user")

            if (user.role != UserRole.OWNER) {
                return Result.failure(
                    Exception("Somente o proprietário pode excluir clientes")
                )
            }
            val customer = customerDao.getCustomer(id).first()
                ?: return Result.failure(
                    Exception("Cliente não encontrado no banco local")
                )

            if (customer.deletedAt != null) {
                return Result.success(Unit)
            }

            if (customer.syncStatus == SyncStatus.SYNCED) {
                val customerRef = firestore
                    .collection("owners")
                    .document(user.uid)
                    .collection("customers")
                    .document(id.toString())

                customerRef.update(
                    "deletedAt",
                    FieldValue.serverTimestamp()
                ).await()
            }

            // Clientes PENDING são excluídos apenas localmente.
            // Clientes SYNCED só chegam aqui após a confirmação remota.
            val rowsUpdated = customerDao.softDeleteCustomer(
                customerId = id,
                deletedAt = System.currentTimeMillis()
            )

            if (rowsUpdated == 0) {
                return Result.failure(
                    Exception("Não foi possível atualizar o cliente local")
                )
            }

            Result.success(Unit)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d("DeleteCustomer", "${e.message}")
            Result.failure(e)
        }
    }


    override fun getLastPurchase(customerId: Uuid?): Flow<PurchaseWithItemsDomain?> {
        return purchaseDao.getLastPurchase(customerId).map { it?.toDomain() }
    }

    override fun getAllPurchases(customerId: Uuid?): Flow<List<PurchaseWithItemsDomain>> {
        return purchaseDao.getAllPurchases(customerId).map { purchaseListData ->
            Log.d("CustomerRepositoryImpl", "getPurchase: $purchaseListData")
            purchaseListData.map { it.toDomain() }
        }
    }

    override fun getAllPayments(customerId: Uuid?): Flow<List<Payment>> {
        return paymentDao.getPayments(customerId).map { paymentList ->
            Log.d("CustomerRepositoryImpl", "getPayments: $paymentList")
            paymentList.map { it.toDomain() }
        }
    }

    override fun searchCustomer(query: String, cityId: Uuid?): Flow<List<Customer>> {
        return customerDao.searchCustomer(query, cityId).map { customerList ->
            customerList.map { it.toDomain() }
        }
    }

    override suspend fun partialPayment(customer: Customer, purchase: Purchase, partialPayment: Payment): Boolean {
        val customerEntity = customer.toCustomerEntity()
        val purchaseEntity = purchase.toEntity()
        val partialPaymentEntity = partialPayment.toEntity()

        return appDatabase.withTransaction {
            val customerRows = customerDao.updateCustomer(customerEntity)
            val purchaseRow = purchaseDao.updatePurchase(purchaseEntity)
            val paymentId = paymentDao.insertPayment(partialPaymentEntity)

            customerRows > 0 && purchaseRow > 0 && paymentId > 0
        }
    }


    private suspend fun getOwnerId(): Result<String> {
        val firebaseUid = auth.currentUser?.uid
            ?: return Result.failure(
                Exception("Usuário não está autenticado")
            )

        val user = userRepository.getCurrentUser(firebaseUid)
            .getOrElse {
                return Result.failure(it)
            }

        val ownerId = when (user.role) {
            UserRole.OWNER -> user.uid
            UserRole.SELLER -> user.ownerId
        }

        return if (ownerId.isNullOrBlank()) {
            Result.failure(
                Exception("Não foi possível identificar o proprietário")
            )
        } else {
            Result.success(ownerId)
        }
    }
}
