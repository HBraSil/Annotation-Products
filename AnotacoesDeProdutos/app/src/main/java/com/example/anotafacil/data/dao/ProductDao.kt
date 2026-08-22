package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.anotafacil.data.entity.ProductEntity


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(product: List<ProductEntity>): List<Long>

    @Query("SELECT * FROM product")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query(" SELECT * FROM product WHERE price > 0")
    suspend fun getProductsWithDefinedPrice(): List<ProductEntity>

    @Update
    suspend fun updateProductPrice(product: ProductEntity): Int


    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT * FROM product WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<ProductEntity>
}