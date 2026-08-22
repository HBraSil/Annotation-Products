package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomer(id: Long): Flow<Customer>
    fun getAllCustomers(cityId: Long): Flow<List<Customer>>
    suspend fun addCustomer(customer: Customer): Long

    suspend fun newPurchase(purchase: Purchase): Long

    suspend fun updateCustomer(customer: Customer): Int
    suspend fun payOffTotalDebt(customer: Customer, payment: Payment): Pair<Int, Long>
    fun getLastPurchase(customerId: Long): Flow<PurchaseWithItemsDomain?>

    suspend fun saveCartItems(cartItems: List<CartItem>): List<Long>
    suspend fun deleteCustomer(customerId: Long): Int

    fun getAllPurchases(customerId: Long): Flow<List<PurchaseWithItemsDomain>>

    fun getAllPayments(customerId: Long): Flow<List<Payment>>

    fun searchCustomer(query: String, cityId: Long): Flow<List<Customer>>

    suspend fun partialPayment(customer: Customer, purchase: Purchase, partialPayment: Payment): Boolean
}