package com.example.anotafacil.domain.model

data class PurchaseWithItemsDomain(
    val purchase: Purchase,
    val items: List<CartItemWithProductDomain>
)

data class CartItemWithProductDomain(
    val cartItem: CartItem,
    val product: Product
)
