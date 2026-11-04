package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anotafacil.data.entity.CartItemEntity
import com.example.anotafacil.data.entity.PurchaseEntity
import com.example.anotafacil.domain.model.MonthlySalesData
import com.example.anotafacil.domain.model.PurchaseWithItemsData
import com.example.anotafacil.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PurchaseDao {
    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
        months.month,
        COALESCE((
            SELECT SUM(p.totalAmount)
            FROM purchase AS p
            LEFT JOIN customer AS c
                ON c.id = p.customerId
            WHERE strftime('%Y-%m', datetime(p.purchaseDate / 1000, 'unixepoch', 'localtime')) = months.month
              AND p.purchaseDate >= :startDate
              AND p.purchaseDate < :endDate
              AND (:cityId IS NULL OR c.cityId = :cityId)
        ), 0) AS totalSales,
        COALESCE((
            SELECT SUM(pay.amount)
            FROM payment AS pay
            LEFT JOIN customer AS c
                ON c.id = pay.customerId
            WHERE strftime('%Y-%m', datetime(pay.paymentDate / 1000, 'unixepoch', 'localtime')) = months.month
              AND pay.paymentDate >= :startDate
              AND pay.paymentDate < :endDate
              AND (:cityId IS NULL OR c.cityId = :cityId)
        ), 0) AS totalReceived,
        COALESCE((
            SELECT SUM(ci.quantity)
            FROM cart_item AS ci
            INNER JOIN purchase AS p
                ON p.id = ci.purchaseId
            LEFT JOIN customer AS c
                ON c.id = p.customerId
            WHERE strftime('%Y-%m', datetime(p.purchaseDate / 1000, 'unixepoch', 'localtime')) = months.month
              AND p.purchaseDate >= :startDate
              AND p.purchaseDate < :endDate
              AND (:cityId IS NULL OR c.cityId = :cityId)
        ), 0) AS totalProducts
    FROM (
        SELECT DISTINCT strftime('%Y-%m', datetime(purchaseDate / 1000, 'unixepoch', 'localtime')) AS month
        FROM purchase
        WHERE purchaseDate >= :startDate
          AND purchaseDate < :endDate
        UNION
        SELECT DISTINCT strftime('%Y-%m', datetime(paymentDate / 1000, 'unixepoch', 'localtime')) AS month
        FROM payment
        WHERE paymentDate >= :startDate
          AND paymentDate < :endDate
    ) AS months
    ORDER BY months.month ASC
""")
    fun getMonthlySales(
        startDate: Long,
        endDate: Long,
        cityId: Uuid? = null
    ): Flow<List<MonthlySalesData>>

    @Query("""
    UPDATE purchase
    SET syncStatus = :status
    WHERE id = :purchaseId
""")
    suspend fun updateSyncStatus(
        purchaseId: Uuid,
        status: SyncStatus
    ): Int

    @Transaction
    @Query("""
    SELECT * FROM purchase
    WHERE syncStatus = :status
    ORDER BY purchaseDate ASC
""")
    suspend fun getPurchasesBySyncStatus(
        status: SyncStatus
    ): List<PurchaseWithItemsData>

    @Query("""
    SELECT *
    FROM purchase
    WHERE id = :purchaseId
""")
    suspend fun getPurchaseById(
        purchaseId: Uuid
    ): PurchaseEntity?


    @Query("DELETE FROM purchase")
    suspend fun deleteAll()


    @Query("DELETE FROM cart_item")
    suspend fun deleteAllCartItems()
}