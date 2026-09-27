package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.entity.toProductDomain
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.SyncStatus
import com.example.anotafacil.domain.repository.ProductRepository
import javax.inject.Inject
import kotlin.uuid.Uuid

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts().map { it.toProductDomain() }
    }

    override suspend fun getProductsWithDefinedPrice(): List<Product> {
        return productDao.getProductsWithDefinedPrice().map { it.toProductDomain() }
    }

    override suspend fun updateProductPrice(productId: Uuid, newPrice: Int): Int {
        val product = productDao.getById(productId)
        val updatedProduct = product.copy(price = newPrice, syncStatus = SyncStatus.PENDING)

        return productDao.updateProductPrice(updatedProduct)
    }
}