package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.Purchase
import kotlin.uuid.Uuid

@Entity(
    tableName = "purchase",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PurchaseEntity(
    @PrimaryKey
    val id: Uuid = Uuid.NIL,
    val customerId: Uuid = Uuid.NIL,
    val sellerId: Uuid = Uuid.NIL,
    val purchaseDate: Long = 0,
    val totalAmount: Double = 0.0,
)

fun PurchaseEntity.toDomain() = Purchase(
    id = id,
    customerId = customerId,
    sellerId = sellerId,
    purchaseDate = purchaseDate,
    totalAmount = totalAmount,
)