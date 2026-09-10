package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CustomerRepository {
    fun getCustomer(id: Uuid?): Flow<Customer?>
    fun getAllCustomers(cityId: Uuid?): Flow<Result<List<Customer>>>
    suspend fun addCustomer(customer: Customer): Result<Boolean>

    suspend fun newPurchase(purchase: Purchase): Result<Boolean>

    suspend fun updateCustomer(customer: Customer): Int
    suspend fun payOffTotalDebt(customer: Customer, payment: Payment): Pair<Int, Long>
    fun getLastPurchase(customerId: Uuid?): Flow<PurchaseWithItemsDomain?>

    suspend fun saveCartItems(cartItems: List<CartItem>): List<Long>
    suspend fun deleteCustomer(customerId: Uuid?): Int

    fun getAllPurchases(customerId: Uuid?): Flow<List<PurchaseWithItemsDomain>>

    fun getAllPayments(customerId: Uuid?): Flow<List<Payment>>

    fun searchCustomer(query: String, cityId: Uuid?): Flow<List<Customer>>

    suspend fun partialPayment(customer: Customer, purchase: Purchase, partialPayment: Payment): Boolean
}