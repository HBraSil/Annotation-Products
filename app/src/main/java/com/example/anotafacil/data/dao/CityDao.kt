package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.anotafacil.data.entity.CityEntity
import com.example.anotafacil.domain.model.SyncStatus
import com.example.anotafacil.presentation.customers.MonthlySalesSummary
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CityDao {

    @Insert
    suspend fun insertCity(city: CityEntity): Long


    @Update
    suspend fun updateCity(city: CityEntity)


    @Query("SELECT * FROM city WHERE ownerId = :ownerId ORDER BY name ASC")
    fun getAll(ownerId: String): List<CityEntity>


    @Query("SELECT * FROM city WHERE id = :id AND ownerId = :ownerId")
    suspend fun getCity(id: Uuid, ownerId: String): CityEntity?


    @Query("""
        SELECT
            city.id,
            city.name,
            city.ownerId,
            city.syncStatus,
            COUNT(customer.id) AS customerCount
        FROM city
        LEFT JOIN customer ON customer.cityId = city.id
        WHERE city.ownerId = :ownerId
        GROUP BY city.id, city.name, city.ownerId, city.syncStatus
        ORDER BY city.name
    """)
    fun getCities(ownerId: String): Flow<List<CityEntity>>

    @Query("""
    SELECT
        city.id,
        city.name,
        city.ownerId,
        city.syncStatus,
        COUNT(customer.id) AS customerCount
    FROM city
    LEFT JOIN customer
        ON customer.cityId = city.id
        AND customer.deletedAt IS NULL
    WHERE city.ownerId = :ownerId
      AND city.name LIKE '%' || :query || '%'
    GROUP BY city.id, city.name, city.ownerId, city.syncStatus
    ORDER BY city.name
    """)
    fun searchCities(query: String, ownerId: String): Flow<List<CityEntity>>


    @Query("""
        SELECT *
        FROM city
        WHERE syncStatus = :status
        AND ownerId = :ownerId
    """)
    suspend fun getCitiesBySyncStatus(
        status: SyncStatus,
        ownerId: String
    ): List<CityEntity>

    @Query("""
        UPDATE city
        SET syncStatus = :status
        WHERE id = :cityId
        AND ownerId = :ownerId
    """)
    suspend fun updateSyncStatus(
        cityId: Uuid,
        status: SyncStatus,
        ownerId: String
    ): Int


    @Query("""SELECT
        COALESCE(SUM(cart_item.quantity), 0) AS totalProducts,
        COALESCE(SUM(cart_item.subtotal), 0) AS totalAmount
    FROM cart_item
    INNER JOIN purchase
        ON purchase.id = cart_item.purchaseId
    INNER JOIN customer
        ON customer.id = purchase.customerId
    INNER JOIN city
        ON city.id = customer.cityId
    WHERE purchase.purchaseDate >= :startMonth
      AND purchase.purchaseDate < :endMonth
      AND customer.cityId = :cityId
      AND city.ownerId = :ownerId""")
    fun getMonthlySalesSummary(cityId: Uuid, startMonth: Long, endMonth: Long, ownerId: String): Flow<MonthlySalesSummary>

    @Query("DELETE FROM city WHERE ownerId = :ownerId")
    suspend fun deleteAll(ownerId: String)
}
