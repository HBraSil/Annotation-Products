package com.example.anotacoesdeprodutos.data.repository

import android.util.Log
import com.example.anotacoesdeprodutos.data.dao.CityDao
import com.example.anotacoesdeprodutos.data.entity.toCity
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.domain.model.toCityEntity
import com.example.anotacoesdeprodutos.domain.repository.CityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao
) : CityRepository {

    override fun getCities(): Flow<List<City>> = cityDao.getAll().map { cityList ->
        cityList.map { it.toCity() }
    }

    override suspend fun addCity(city: City): Long {
        val newCity = city.toCityEntity()
        return cityDao.addCity(newCity)
    }

    override fun getCitiesWithClientCount(): Flow<List<City>> {
       return cityDao.getCitiesWithCustomerCount().map { cityList ->
            cityList.map {
                Log.d("CityRepository", it.toCity().name)
                it.toCity()
            }
        }
    }

    override suspend fun getCity(cityId: Long?): City? {
        return cityDao.getCity(cityId)?.toCity()
    }
}
