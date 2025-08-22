package com.example.anotacoesdeprodutos.data.repository

import android.util.Log
import com.example.anotacoesdeprodutos.data.dao.CustomerDao
import com.example.anotacoesdeprodutos.data.entity.toDomain
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Purchase
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItems
import com.example.anotacoesdeprodutos.domain.model.toCartEntity
import com.example.anotacoesdeprodutos.domain.model.toCustomerEntity
import com.example.anotacoesdeprodutos.domain.model.toEntity
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao
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

    override suspend fun addPurchase(purchase: Purchase): Long {
        Log.d("CustomerRepositoryImpl", "Adding purchase: $purchase")
        return customerDao.addPurchase(purchase.toEntity())
    }

    override suspend fun saveCartItems(cartItems: List<CartItem>): List<Long> {
        Log.d("CustomerRepositoryImpl", "Saving cart items: $cartItems")
        return customerDao.saveCartItems(cartItems.map { it.toCartEntity() })
    }

    override fun getLastPurchase(customerId: Long): Flow<PurchaseWithItems?> {
        Log.d("CustomerRepositoryImpl", "Getting last purchase for customer ID: $customerId")
        return customerDao.getLastPurchase(customerId)
    }
/*
    override suspend fun getCartItems(cartItems: List<CartItem>): List<Long> {
        return customerDao.getCartItems(cartItems.map { it.toCartEntity() })
    }*/
}
