package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.entity.toProductDomain
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts().map { it.toProductDomain() }
    }

    override suspend fun getProductsWithDefinedPrice(): List<Product> {
        return productDao.getProductsWithDefinedPrice().map { it.toProductDomain() }
    }

    override suspend fun updateProductPrice(productId: Long, newPrice: Int): Int {
        val product = productDao.getById(productId)
        return product?.let {
            val updatedProduct = it.copy(price = newPrice)
            productDao.updateProductPrice(updatedProduct)
        } ?: 0
    }
}