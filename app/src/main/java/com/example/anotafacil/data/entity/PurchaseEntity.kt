package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.SyncStatus
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
    val ownerId: String = "",
    val purchaseDate: Long = 0,
    val totalAmount: Double = 0.0,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

fun PurchaseEntity.toDomain() = Purchase(
    id = id,
    customerId = customerId,
    ownerId = ownerId,
    purchaseDate = purchaseDate,
    totalAmount = totalAmount,
)