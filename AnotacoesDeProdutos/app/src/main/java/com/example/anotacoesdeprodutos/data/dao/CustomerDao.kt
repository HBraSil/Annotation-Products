package com.example.anotacoesdeprodutos.data.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anotacoesdeprodutos.data.entity.CartItemEntity
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.data.entity.CustomerEntity
import com.example.anotacoesdeprodutos.data.entity.PaymentEntity
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsData
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
    suspend fun addPurchase(purchase: PurchaseEntity): Long

    @Transaction
    @Query("""
        SELECT * FROM purchase
        WHERE customerId = :customerId
        ORDER BY id DESC
        LIMIT 1
    """)
    fun getLastPurchase(customerId: Long): Flow<PurchaseWithItemsData?>

    @Transaction
    @Query("""
        SELECT * FROM purchase
        WHERE customerId = :customerId
        ORDER BY purchaseDate DESC
    """)
    fun getAllPurchases(customerId: Long): Flow<List<PurchaseWithItemsData>>


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

    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity): Int

    @Query("SELECT * FROM purchase WHERE id = :id")
    suspend fun getPurchase(id: Long): PurchaseEntity?


    @Transaction
    suspend fun registerPartialPayment(
        customer: CustomerEntity,
        purchase: PurchaseEntity,
        partialPayment: PaymentEntity
    ): Boolean {
        Log.d("CustomerDao", "registerPartialPayment: $partialPayment")
        val customerRows = updateCustomer(customer)
        val purchaseRow = updatePurchase(purchase)
        val paymentId = insertPayment(partialPayment)

        return customerRows > 0 && purchaseRow > 0 && paymentId > 0
    }
}