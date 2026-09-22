package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.anotafacil.data.entity.CartItemEntity
import com.example.anotafacil.data.entity.CustomerEntity
import com.example.anotafacil.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customer WHERE cityId = :cityId")
    fun getAll(cityId: Uuid?): Flow<List<CustomerEntity>>


    @Query("SELECT * FROM customer WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<CustomerEntity>

    @Query("SELECT * FROM customer WHERE id = :id")
    fun getCustomer(id: Uuid?): Flow<CustomerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCustomer(customer: CustomerEntity)

    @Query("DELETE FROM Customer WHERE id = :customerId")
    suspend fun deleteCustomer(customerId: Uuid?): Int

    @Insert
    suspend fun saveCartItems(cartItems: List<CartItemEntity>): List<Long>

    @Query("""
    SELECT *
    FROM customer
    WHERE cityId = :cityId
""")
    suspend fun getCustomersByCity(
        cityId: Uuid
    ): List<CustomerEntity>

    @Query("""
    SELECT *
    FROM customer
    WHERE syncStatus = :status
""")
    suspend fun getCustomersBySyncStatus(
        status: SyncStatus
    ): List<CustomerEntity>

    @Query("""
    UPDATE customer
    SET syncStatus = :status
    WHERE id = :customerId
""")
    suspend fun updateSyncStatus(
        customerId: Uuid,
        status: SyncStatus
    ): Int

    @Query("""
        SELECT *
        FROM customer
        WHERE cityId = :cityId
        AND name LIKE '%' || :query || '%'
        ORDER BY name
    """)
    fun searchCustomer(query: String, cityId: Uuid?): Flow<List<CustomerEntity>>

    @Update
    suspend fun updateCustomer(customer: CustomerEntity): Int
}