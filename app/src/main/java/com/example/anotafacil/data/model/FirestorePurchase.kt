package com.example.anotafacil.data.model


data class FirestorePurchase(
    val customerId: String = "",
    val purchaseDate: Long = 0,
    val totalAmount: Double = 0.0,
    val items: List<FirestoreCartItem> = emptyList()
)

data class FirestoreCartItem(
    val productId: String = "",
    val quantity: Int = 0,
    val unitPrice: Int = 0,
    val subtotal: Int = 0
)
