package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.entity.toCity
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.toCityEntity
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.presentation.customers.MonthlySalesSummary
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map
import kotlin.uuid.Uuid

class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao
) : CityRepository {

    override fun getCities(): Flow<List<City>> = cityDao.getCities().map { cityList ->
        cityList.map {
            it.toCity()
        }
    }

    override suspend fun addCity(city: City): Long {
        val newCity = city.toCityEntity()
        return cityDao.addCity(newCity)
    }

    override suspend fun getCity(cityId: Uuid?): Result<City?> {
        return if (cityId == null)
            Result.failure(Exception("cityId não existe"))
        else
            Result.success(cityDao.getCity(cityId)?.toCity())
    }

    override fun searchCities(query: String): Flow<List<City>> {
        return cityDao.searchCities(query).map { cityList ->
            cityList.map { it.toCity() }
        }
    }

    override fun getMonthlySalesSummary(
        cityId: Uuid?,
        startMonth: Long,
        endMonth: Long,
    ): Flow<MonthlySalesSummary> {
        if (cityId == null) {
            throw IllegalArgumentException("cityId cannot be null")
        }

        return cityDao.getMonthlySalesSummary(cityId, startMonth, endMonth)
    }
}
