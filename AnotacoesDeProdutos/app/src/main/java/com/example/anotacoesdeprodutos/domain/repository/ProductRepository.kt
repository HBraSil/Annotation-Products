package com.example.anotacoesdeprodutos.domain.repository

import com.example.anotacoesdeprodutos.domain.model.Product

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>

    suspend fun getProductsWithDefinedPrice(): List<Product>

    suspend fun updateProductPrice(productId: Long, newPrice: Int): Int
}