package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anotafacil.data.entity.CityEntity
import com.example.anotafacil.domain.model.SyncStatus
import com.example.anotafacil.presentation.customers.MonthlySalesSummary
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAll(): Flow<List<CityEntity>>


    @Query("SELECT * FROM city WHERE id = :id")
    suspend fun getCity(id: Uuid): CityEntity?


    @Query("""
        SELECT
            city.id,
            city.name,
            city.syncStatus,
            COUNT(customer.id) AS customerCount
        FROM city
        LEFT JOIN customer ON customer.cityId = city.id
        GROUP BY city.id, city.name, city.syncStatus
        ORDER BY city.name
    """)
    fun getCities(): Flow<List<CityEntity>>

    @Query("""
    SELECT
        city.id,
        city.name,
        city.syncStatus,
        COUNT(customer.id) AS customerCount
    FROM city
    LEFT JOIN customer
        ON customer.cityId = city.id
    WHERE city.name LIKE '%' || :query || '%'
    GROUP BY city.id, city.name, city.syncStatus
    ORDER BY city.name
""")
    fun searchCities(query: String): Flow<List<CityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCity(city: CityEntity): Long


    @Query("""
        SELECT *
        FROM city
        WHERE syncStatus = :status
    """)
    suspend fun getCitiesBySyncStatus(
        status: SyncStatus
    ): List<CityEntity>

    @Query("""
        UPDATE city
        SET syncStatus = :status
        WHERE id = :cityId
    """)
    suspend fun updateSyncStatus(
        cityId: Uuid,
        status: SyncStatus
    ): Int


    @Query("""SELECT
        COALESCE(SUM(cart_item.quantity), 0) AS totalProducts,
        COALESCE(SUM(cart_item.subtotal), 0) AS totalAmount
    FROM cart_item
    INNER JOIN purchase
        ON purchase.id = cart_item.purchaseId
    INNER JOIN customer
        ON customer.id = purchase.customerId
    WHERE purchase.purchaseDate >= :startMonth
      AND purchase.purchaseDate < :endMonth
      AND customer.cityId = :cityId""")
    fun getMonthlySalesSummary(cityId: Uuid, startMonth: Long, endMonth: Long): Flow<MonthlySalesSummary>
}
