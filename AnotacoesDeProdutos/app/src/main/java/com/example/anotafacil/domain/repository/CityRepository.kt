package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.City
import com.example.anotafacil.presentation.customers.MonthlySalesSummary
import kotlinx.coroutines.flow.Flow


interface CityRepository {
    fun getCities(): Flow<List<City>>

    suspend fun addCity(city: City): Long

    suspend fun getCity(cityId: Long?): City?

    fun searchCities(query: String): Flow<List<City>>

    fun getMonthlySalesSummary(cityId: Long, startMonth: Long, endMonth: Long): Flow<MonthlySalesSummary>
}