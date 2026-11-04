package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.entity.toCity
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toCityEntity
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.presentation.customers.MonthlySalesSummary
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val userRepository: UserRepository
) : CityRepository {

    private suspend fun currentOwnerId(): String? {
        val currentUser = userRepository.getCurrentUser().getOrNull() ?: return null
        return when (currentUser.role) {
            UserRole.OWNER -> currentUser.uid
            UserRole.SELLER -> currentUser.ownerId
        }
    }

    override fun getCities(): Flow<List<City>> = flow {
        val ownerId = currentOwnerId() ?: run {
            emit(emptyList())
            return@flow
        }
        emitAll(cityDao.getCities(ownerId).map { cityList ->
            cityList.map { it.toCity() }
        })
    }

    override suspend fun addCity(city: City): Long {
        val ownerId = currentOwnerId() ?: return 0L
        val newCity = city.copy(ownerId = ownerId).toCityEntity()
        return cityDao.insertCity(newCity)
    }

    override suspend fun getCity(cityId: Uuid?): Result<City?> {
        val ownerId = currentOwnerId() ?: return Result.failure(Exception("Não foi possível identificar o proprietário"))
        return if (cityId == null) {
            Result.failure(Exception("cityId não existe"))
        } else {
            Result.success(cityDao.getCity(cityId, ownerId)?.toCity())
        }
    }

    override fun searchCities(query: String): Flow<List<City>> = flow {
        val ownerId = currentOwnerId() ?: run {
            emit(emptyList())
            return@flow
        }
        emitAll(cityDao.searchCities(query, ownerId).map { cityList ->
            cityList.map { it.toCity() }
        })
    }

    override fun getMonthlySalesSummary(
        cityId: Uuid?,
        startMonth: Long,
        endMonth: Long,
    ): Flow<MonthlySalesSummary> = flow {
        if (cityId == null) {
            throw IllegalArgumentException("cityId cannot be null")
        }

        val ownerId = currentOwnerId() ?: throw IllegalStateException("Owner not found")
        emitAll(cityDao.getMonthlySalesSummary(cityId, startMonth, endMonth, ownerId))
    }
}
