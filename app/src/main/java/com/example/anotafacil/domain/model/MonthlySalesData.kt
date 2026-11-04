package com.example.anotafacil.domain.model

data class MonthlySalesData(
    val month: String = "",
    val totalSales: Double = 0.0,
    val totalReceived: Double = 0.0,
    val totalProducts: Int = 0,
)
