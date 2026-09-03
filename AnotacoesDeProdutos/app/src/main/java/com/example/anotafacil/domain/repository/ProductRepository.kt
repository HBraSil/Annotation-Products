package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.Product
import kotlin.uuid.Uuid

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>

    suspend fun getProductsWithDefinedPrice(): List<Product>

    suspend fun updateProductPrice(productId: Uuid, newPrice: Int): Int
}