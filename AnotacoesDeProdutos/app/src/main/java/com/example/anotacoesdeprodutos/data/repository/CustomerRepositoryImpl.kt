package com.example.anotacoesdeprodutos.data.repository

import android.util.Log
import androidx.room.Transaction
import androidx.room.withTransaction
import com.example.anotacoesdeprodutos.data.dao.CustomerDao
import com.example.anotacoesdeprodutos.data.dao.PurchaseDao
import com.example.anotacoesdeprodutos.data.entity.CustomerEntity
import com.example.anotacoesdeprodutos.data.entity.PaymentEntity
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.data.entity.toDomain
import com.example.anotacoesdeprodutos.data.network.AppDatabase
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Payment
import com.example.anotacoesdeprodutos.domain.model.Purchase
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsDomain
import com.example.anotacoesdeprodutos.domain.model.toCartEntity
import com.example.anotacoesdeprodutos.domain.model.toCustomerEntity
import com.example.anotacoesdeprodutos.domain.model.toDomain
import com.example.anotacoesdeprodutos.domain.model.toEntity
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    private val purchaseDao: PurchaseDao,
    val appDatabase: AppDatabase
) : CustomerRepository {
    override fun getCustomer(id: Long): Flow<Customer> {
        return customerDao.getCustomer(id).map { it.toDomain() }
    }

    override fun getAllCustomers(cityId: Long): Flow<List<Customer>> = customerDao.getAll(cityId).map { customerList ->
        customerList.map { it.toDomain() }
    }

    override suspend fun addCustomer(customer: Customer): Long {
        return customerDao.saveCustomer(customer.toCustomerEntity())
    }

    override suspend fun newPurchase(purchase: Purchase): Long {
        return purchaseDao.addPurchase(purchase.toEntity())
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

    override suspend fun deleteCustomer(customerId: Long): Int {
        return customerDao.deleteCustomer(customerId)
    }

    override fun getLastPurchase(customerId: Long): Flow<PurchaseWithItemsDomain?> {
        return purchaseDao.getLastPurchase(customerId).map { it?.toDomain() }
    }

    override fun getAllPurchases(customerId: Long): Flow<List<PurchaseWithItemsDomain>> {
        return purchaseDao.getAllPurchases(customerId).map { purchaseListData ->
            Log.d("CustomerRepositoryImpl", "getPurchase: $purchaseListData")
            purchaseListData.map { it.toDomain() }
        }
    }

    override fun getAllPayments(customerId: Long): Flow<List<Payment>> {
        return customerDao.getPayments(customerId).map { paymentList ->
            Log.d("CustomerRepositoryImpl", "getPayments: $paymentList")
            paymentList.map { it.toDomain() }
        }
    }

    override fun searchCustomer(query: String, cityId: Long): Flow<List<Customer>> {
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
