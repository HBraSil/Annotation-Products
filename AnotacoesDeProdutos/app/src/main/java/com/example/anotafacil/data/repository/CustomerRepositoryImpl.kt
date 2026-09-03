package com.example.anotafacil.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.data.entity.toDomain
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import com.example.anotafacil.domain.model.toCartEntity
import com.example.anotafacil.domain.model.toCustomerEntity
import com.example.anotafacil.domain.model.toDomain
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.uuid.Uuid

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    private val purchaseDao: PurchaseDao,
    val appDatabase: AppDatabase
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
            println(
                "ANALISAR ----> ${customer.toCustomerEntity()}"
            )
            customerDao.saveCustomer(customer.toCustomerEntity())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Error ao adicionar cliente"))
        }
    }

    override suspend fun newPurchase(purchase: Purchase): Result<Boolean> {
        return try {
            purchaseDao.addPurchase(purchase.toEntity())
            Result.success(true)
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
            customerDao.insertPayment(payment.toEntity())
        )
    }

    override suspend fun saveCartItems(cartItems: List<CartItem>): List<Long> {
        return customerDao.saveCartItems(cartItems.map { it.toCartEntity() })
    }

    override suspend fun deleteCustomer(customerId: Uuid?): Int {
        return customerDao.deleteCustomer(customerId)
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
        return customerDao.getPayments(customerId).map { paymentList ->
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
            val paymentId = customerDao.insertPayment(partialPaymentEntity)

            customerRows > 0 && purchaseRow > 0 && paymentId > 0
        }
    }
}
