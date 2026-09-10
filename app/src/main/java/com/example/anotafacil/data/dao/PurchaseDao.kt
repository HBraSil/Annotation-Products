package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anotafacil.data.entity.PurchaseEntity
import com.example.anotafacil.domain.model.MonthlySalesData
import com.example.anotafacil.domain.model.PurchaseWithItemsData
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PurchaseDao {
    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity): Int

    @Insert
    suspend fun addPurchase(purchase: PurchaseEntity)

    @Transaction
    @Query("""
        SELECT * FROM purchase
        WHERE customerId = :customerId
        ORDER BY id DESC
        LIMIT 1
    """)
    fun getLastPurchase(customerId: Uuid?): Flow<PurchaseWithItemsData?>

    @Transaction
    @Query("""
        SELECT * FROM purchase
        WHERE customerId = :customerId
        ORDER BY purchaseDate DESC
    """)
    fun getAllPurchases(customerId: Uuid?): Flow<List<PurchaseWithItemsData>>

    @Query("""
    SELECT
        strftime(
            '%Y-%m',
            datetime(
                p.purchaseDate / 1000,
                'unixepoch',
                'localtime'
            )
        ) AS month,

        SUM(ci.quantity) AS totalQuantity

    FROM purchase AS p

    INNER JOIN cart_item AS ci
        ON ci.purchaseId = p.id

    WHERE p.purchaseDate >= :startDate
      AND p.purchaseDate < :endDate

    GROUP BY month

    ORDER BY month ASC
""")
    fun getMonthlySales(
        startDate: Long,
        endDate: Long
    ): Flow<List<MonthlySalesData>>
}