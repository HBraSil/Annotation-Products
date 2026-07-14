package com.example.anotacoesdeprodutos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsData
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity): Int

    @Query("SELECT * FROM purchase WHERE id = :id")
    suspend fun getPurchase(id: Long): PurchaseEntity?

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
}