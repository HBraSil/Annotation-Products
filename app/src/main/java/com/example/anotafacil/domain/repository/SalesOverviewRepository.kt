package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.MonthlySalesData
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface SalesOverviewRepository {
    fun getMonthlySales(startDate: Long, endDate: Long, cityId: Uuid? = null): Flow<List<MonthlySalesData>>
}