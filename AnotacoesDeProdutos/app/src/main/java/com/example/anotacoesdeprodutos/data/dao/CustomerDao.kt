package com.example.anotacoesdeprodutos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.anotacoesdeprodutos.data.entity.CartItemEntity
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.data.entity.CustomerEntity
import com.example.anotacoesdeprodutos.data.entity.PaymentStatus
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsData
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customer WHERE cityId = :cityId")
    fun getAll(cityId: Long): Flow<List<CustomerEntity>>


    @Query("SELECT * FROM customer WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<CustomerEntity>

    @Query("SELECT * FROM customer WHERE status = :status")
    suspend fun getByStatus(status: PaymentStatus): List<CustomerEntity>

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
}