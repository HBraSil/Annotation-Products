package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.entity.ProductEntity
import com.example.anotafacil.data.entity.toProductDomain
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.SyncStatus
import com.example.anotafacil.domain.model.toProductEntity
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

    override suspend fun saveProductChanges(product: Product): Boolean {
        val productFromEntity = productDao.getById(product.id)

        if(productFromEntity == null){
            val productEntity = product.toProductEntity()
            return productDao.insert(productEntity) > 0
        }

        val updatedProduct = productFromEntity.copy(price = product.price, syncStatus = SyncStatus.PENDING)

        return productDao.updateProductPrice(updatedProduct) > 0
    }
}