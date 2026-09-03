package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.domain.model.MonthlySalesData
import com.example.anotafacil.domain.repository.SalesOverviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SalesOverviewRepositoryImpl @Inject constructor(
    private val purchaseDao: PurchaseDao
): SalesOverviewRepository {
    override fun getMonthlySales(
        startDate: Long,
        endDate: Long,
    ): Flow<List<MonthlySalesData>> {
        return purchaseDao.getMonthlySales(startDate, endDate)
    }

}