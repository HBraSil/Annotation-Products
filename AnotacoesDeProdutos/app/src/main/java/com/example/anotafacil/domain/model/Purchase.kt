package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.PurchaseEntity

data class Purchase(
    val id: Long = 0,
    val customerId: Long = 0,
    val purchaseDate: Long = 0,
    val totalAmount: Double = 0.0,
    val items: List<CartItem> = emptyList()
)

fun Purchase.toEntity() = PurchaseEntity(
    id = id,
    customerId = customerId,
    purchaseDate = purchaseDate,
    totalAmount = totalAmount,
)