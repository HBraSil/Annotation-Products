package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.CustomerEntity
import kotlin.uuid.Uuid

data class Customer(
    val id: Uuid? = null,
    val name: String = "",
    val owes: Double? = null,
    val lastPurchase: List<CartItem> = emptyList(),
    val lastPurchaseDate: String? = null,
    val extraInfo: String? = null,
    val cityId: Uuid? = null
)

fun Customer.toCustomerEntity() = CustomerEntity(
    id = id ?: Uuid.random(),
    name = name,
    lastPurchaseDate = lastPurchaseDate,
    owes = owes,
    extraInfo = extraInfo,
    cityId = cityId ?: Uuid.random()
)
