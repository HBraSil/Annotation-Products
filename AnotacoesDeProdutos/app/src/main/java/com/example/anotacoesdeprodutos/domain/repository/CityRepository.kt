package com.example.anotacoesdeprodutos.domain.repository

import com.example.anotacoesdeprodutos.domain.model.City
import kotlinx.coroutines.flow.Flow


interface CityRepository {
    fun getCities(): Flow<List<City>>

    suspend fun addCity(city: City): Long

    suspend fun getCity(cityId: Long?): City?

    fun searchCities(query: String): Flow<List<City>>
}