package com.example.anotacoesdeprodutos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotacoesdeprodutos.domain.model.Purchase

@Entity(tableName = "purchase")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long = 0,
    val purchaseDate: Long = 0,
    val total: Double = 0.0
)

fun PurchaseEntity.toDomain() = Purchase(
    id = id,
    customerId = customerId,
    purchaseDate = purchaseDate,
    total = total
)