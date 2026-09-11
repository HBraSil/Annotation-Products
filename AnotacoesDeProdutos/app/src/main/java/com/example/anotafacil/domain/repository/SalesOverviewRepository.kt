package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.MonthlySalesData
import kotlinx.coroutines.flow.Flow

interface SalesOverviewRepository {
    fun getMonthlySales(startDate: Long, endDate: Long): Flow<List<MonthlySalesData>>
}