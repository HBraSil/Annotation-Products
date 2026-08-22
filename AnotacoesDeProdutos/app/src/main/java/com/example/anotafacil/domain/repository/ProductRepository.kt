package com.example.anotafacil.domain.repository

import com.example.anotafacil.domain.model.Product

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>

    suspend fun getProductsWithDefinedPrice(): List<Product>

    suspend fun updateProductPrice(productId: Long, newPrice: Int): Int
}