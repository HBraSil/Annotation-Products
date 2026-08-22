package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.CustomerEntity

data class Customer(
    val id: Long = 0,
    val name: String = "",
    val owes: Double? = null,
    val lastPurchase: List<CartItem> = emptyList(),
    val lastPurchaseDate: String? = null,
    val extraInfo: String? = null,
    val cityId: Long = 0
)

fun Customer.toCustomerEntity() = CustomerEntity(
    id = id,
    name = name,
    lastPurchaseDate = lastPurchaseDate,
    owes = owes,
    extraInfo = extraInfo,
    cityId = cityId
)
