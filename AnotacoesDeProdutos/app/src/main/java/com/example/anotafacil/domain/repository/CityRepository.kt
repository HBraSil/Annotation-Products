package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.City
import com.example.anotafacil.presentation.customers.MonthlySalesSummary
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


interface CityRepository {
    fun getCities(): Flow<List<City>>

    suspend fun addCity(city: City): Long

    suspend fun getCity(cityId: Uuid?): Result<City?>

    fun searchCities(query: String): Flow<List<City>>

    fun getMonthlySalesSummary(cityId: Uuid?, startMonth: Long, endMonth: Long): Flow<MonthlySalesSummary>
}