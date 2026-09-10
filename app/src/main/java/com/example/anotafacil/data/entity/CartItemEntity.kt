package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.CartItem
import kotlin.uuid.Uuid

@Entity(
    tableName = "cart_item",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Uuid,
    val productId: Uuid,
    val quantity: Int,
    val unitPrice: Int,
    val subtotal: Int
)

fun CartItemEntity.toCartItemDomain() = CartItem(
    id = id,
    productId = productId,
    purchaseId = purchaseId,
    quantity = quantity,
)