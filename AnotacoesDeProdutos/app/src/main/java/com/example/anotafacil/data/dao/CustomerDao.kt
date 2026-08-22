package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.anotafacil.data.entity.CartItemEntity
import com.example.anotafacil.data.entity.CustomerEntity
import com.example.anotafacil.data.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customer WHERE cityId = :cityId")
    fun getAll(cityId: Long): Flow<List<CustomerEntity>>


    @Query("SELECT * FROM customer WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<CustomerEntity>

    @Query("SELECT * FROM customer WHERE id = :id")
    fun getCustomer(id: Long): Flow<CustomerEntity>

    @Insert
    suspend fun saveCustomer(customer: CustomerEntity): Long

    @Query("DELETE FROM Customer WHERE id = :customerId")
    suspend fun deleteCustomer(customerId: Long): Int

    @Insert
    suspend fun saveCartItems(cartItems: List<CartItemEntity>): List<Long>

    @Query("""
        SELECT *
        FROM customer
        WHERE cityId = :cityId
        AND name LIKE '%' || :query || '%'
        ORDER BY name
    """)
    fun searchCustomer(query: String, cityId: Long): Flow<List<CustomerEntity>>

    @Update
    suspend fun updateCustomer(customer: CustomerEntity): Int

    @Insert
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query("SELECT * FROM payment WHERE customerId = :customerId")
    fun getPayments(customerId: Long): Flow<List<PaymentEntity>>
}