package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.CartItemEntity
import kotlin.uuid.Uuid

data class CartItem(
    val id: Long = 0,
    val product: Product = Product(),
    val productId: Uuid = Uuid.NIL,
    val purchaseId: Uuid = Uuid.NIL,
    val quantity: Int = 0,
) {
    fun subtotal() = product.price * quantity
}

fun CartItem.toCartEntity() = CartItemEntity(
    id = id,
    quantity = quantity,
    purchaseId = purchaseId,
    productId = product.id,
    unitPrice = product.price,
    subtotal = subtotal()
)