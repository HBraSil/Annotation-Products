package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.anotafacil.data.entity.ProductEntity
import com.example.anotafacil.domain.model.SyncStatus
import kotlin.uuid.Uuid


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(product: List<ProductEntity>): List<Long>

    @Insert
    suspend fun insert(product: ProductEntity): Long


    @Query("""
    SELECT *
    FROM product
    WHERE syncStatus = :status
    AND (ownerId = :ownerId OR ownerId = '')
    """)
    suspend fun getProductsBySyncStatus(
        status: SyncStatus,
        ownerId: String
    ): List<ProductEntity>

    @Query("SELECT * FROM product WHERE (ownerId = :ownerId OR ownerId = '') ORDER BY name ASC")
    suspend fun getAllProducts(ownerId: String): List<ProductEntity>

    @Query("SELECT * FROM product WHERE price > 0 AND (ownerId = :ownerId OR ownerId = '')")
    suspend fun getProductsWithDefinedPrice(ownerId: String): List<ProductEntity>

    @Update
    suspend fun updateProductPrice(product: ProductEntity): Int

    @Query("SELECT * FROM product WHERE id = :id AND (ownerId = :ownerId OR ownerId = '')")
    suspend fun getById(id: Uuid, ownerId: String): ProductEntity?

    @Query("SELECT * FROM product WHERE name LIKE '%' || :query || '%' AND (ownerId = :ownerId OR ownerId = '')")
    suspend fun search(query: String, ownerId: String): List<ProductEntity>

    @Query("DELETE FROM product WHERE ownerId = :ownerId")
    suspend fun deleteAll(ownerId: String)

    @Query("""
    UPDATE product
    SET syncStatus = :status
    WHERE id = :productId
    AND (ownerId = :ownerId OR ownerId = '')
    """)
    suspend fun updateSyncStatus(
        productId: Uuid,
        status: SyncStatus,
        ownerId: String
    ): Int
}