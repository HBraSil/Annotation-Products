package com.example.anotacoesdeprodutos.data.repository

import com.example.anotacoesdeprodutos.data.dao.ProductDao
import com.example.anotacoesdeprodutos.data.entity.toProductDomain
import com.example.anotacoesdeprodutos.domain.model.Product
import com.example.anotacoesdeprodutos.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getAllProducts(): List<Product> {
        return productDao.getAll().map { it.toProductDomain() }
    }

    override suspend fun updateProductPrice(productId: Long, newPrice: Int): Int {
        val product = productDao.getById(productId)
        return product?.let {
            val updatedProduct = it.copy(price = newPrice)
            productDao.updateProductPrice(updatedProduct)
        } ?: 0
    }
}