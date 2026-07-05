package com.example.anotacoesdeprodutos.domain.model

import com.example.anotacoesdeprodutos.data.entity.CustomerEntity

data class Customer(
    val id: Long = 0,
    val name: String = "",
    //val status: PaymentStatus? = null,
    val owes: Double? = null,
    val lastPurchase: List<CartItem> = emptyList(),
    val lastPurchaseDate: String? = null,
    val extraInfo: String? = null,
    val cityId: Long = 0
)

fun Customer.toCustomerEntity() = CustomerEntity(
    id = id,
    name = name,
    //status = status,
    lastPurchaseDate = lastPurchaseDate,
    owes = owes,
    extraInfo = extraInfo,
    cityId = cityId
)
