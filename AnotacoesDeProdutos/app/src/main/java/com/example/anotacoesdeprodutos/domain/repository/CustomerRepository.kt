package com.example.anotacoesdeprodutos.domain.repository

import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Purchase
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsData
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsDomain
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomer(id: Long): Flow<Customer>
    fun getAllCustomers(cityId: Long): Flow<List<Customer>>
    suspend fun addCustomer(customer: Customer): Long

    suspend fun addPurchase(purchase: Purchase): Long
    fun getLastPurchase(customerId: Long): Flow<PurchaseWithItemsData?>

    suspend fun saveCartItems(cartItems: List<CartItem>): List<Long>
    suspend fun deleteCustomer(customerId: Long): Int

    fun getAllPurchases(customerId: Long): Flow<List<PurchaseWithItemsDomain>>
    //suspend fun getCartItems(cartItems: List<CartItem>): List<Long>
}