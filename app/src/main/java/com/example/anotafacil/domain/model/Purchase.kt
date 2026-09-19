package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.PurchaseEntity
import kotlin.uuid.Uuid

data class Purchase(
    val id: Uuid = Uuid.random(),
    val customerId: Uuid? = null,
    val ownerId: String = "",
    val purchaseDate: Long = 0,
    val totalAmount: Double = 0.0,
    val items: List<CartItem> = emptyList()
)

fun Purchase.toEntity() = PurchaseEntity(
    id = id,
    customerId = customerId ?: Uuid.random(),
    ownerId = ownerId,
    purchaseDate = purchaseDate,
    totalAmount = totalAmount,
)