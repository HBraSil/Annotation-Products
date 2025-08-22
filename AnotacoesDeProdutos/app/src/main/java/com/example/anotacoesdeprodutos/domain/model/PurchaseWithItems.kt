package com.example.anotacoesdeprodutos.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.anotacoesdeprodutos.data.entity.CartItemEntity
import com.example.anotacoesdeprodutos.data.entity.ProductEntity
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity

data class PurchaseWithItems(
    @Embedded
    val purchase: PurchaseEntity = PurchaseEntity(),

    @Relation(
        parentColumn = "id",
        entityColumn = "purchaseId",
        entity = CartItemEntity::class
    )
    val items: List<CartItemWithProduct> = emptyList()
)

data class CartItemWithProduct(
    @Embedded val cartItem: CartItemEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)