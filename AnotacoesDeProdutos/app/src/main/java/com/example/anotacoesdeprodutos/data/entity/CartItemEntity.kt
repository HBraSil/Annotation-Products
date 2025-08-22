package com.example.anotacoesdeprodutos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotacoesdeprodutos.domain.model.CartItem

@Entity(tableName = "cart_item")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Long = 0,
    val productId: Long = 0,
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