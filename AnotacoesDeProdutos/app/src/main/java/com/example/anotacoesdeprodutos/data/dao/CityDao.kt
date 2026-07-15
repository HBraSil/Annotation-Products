package com.example.anotacoesdeprodutos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.anotacoesdeprodutos.data.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAll(): Flow<List<CityEntity>>

    @Query("SELECT * FROM city WHERE id = :id")
    suspend fun getById(id: Long): CityEntity?

    @Query("SELECT * FROM city WHERE id = :id")
    suspend fun getCity(id: Long?): CityEntity?


    @Query("""
        SELECT
            city.id,
            city.name,
            COUNT(customer.id) AS customerCount
        FROM city
        LEFT JOIN customer ON customer.cityId = city.id
        GROUP BY city.id
        ORDER BY city.name
    """)
    fun getCities(): Flow<List<CityEntity>>

    @Query("""
    SELECT
        city.id,
        city.name,
        COUNT(customer.id) AS customerCount
    FROM city
    LEFT JOIN customer
        ON customer.cityId = city.id
    WHERE city.name LIKE '%' || :query || '%'
    GROUP BY city.id, city.name
    ORDER BY city.name
""")
    fun searchCities(query: String): Flow<List<CityEntity>>

    @Insert
    suspend fun addCity(city: CityEntity): Long
}
