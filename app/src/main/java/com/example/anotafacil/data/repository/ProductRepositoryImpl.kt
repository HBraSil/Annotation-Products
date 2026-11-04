package com.example.anotafacil.data.repository

import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.entity.toProductDomain
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toProductEntity
import com.example.anotafacil.domain.repository.ProductRepository
import com.example.anotafacil.domain.repository.UserRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val userRepository: UserRepository
) : ProductRepository {
    private suspend fun currentOwnerId(): String? {
        val currentUser = userRepository.getCurrentUser().getOrNull() ?: return null

        return when (currentUser.role) {
            UserRole.OWNER -> currentUser.uid
            UserRole.SELLER -> currentUser.ownerId
        }
    }

    override suspend fun getAllProducts(): List<Product> {
        val ownerId = currentOwnerId() ?: return emptyList()
        return productDao.getAllProducts(ownerId).map { it.toProductDomain() }
    }

    override suspend fun getProductsWithDefinedPrice(): List<Product> {
        val ownerId = currentOwnerId() ?: return emptyList()
        return productDao.getProductsWithDefinedPrice(ownerId).map { it.toProductDomain() }
    }

    override suspend fun saveProductChanges(product: Product): Boolean {
        val ownerId = currentOwnerId() ?: return false
        val productWithOwner = product.copy(ownerId = ownerId)
        val productFromEntity = productDao.getById(productWithOwner.id, ownerId)

        if (productFromEntity == null) {
            val productEntity = productWithOwner.toProductEntity()
            return productDao.insert(productEntity) > 0
        }

        val updatedProduct = productFromEntity.copy(
            name = productWithOwner.name,
            price = productWithOwner.price,
            ownerId = ownerId,
            syncStatus = com.example.anotafacil.domain.model.SyncStatus.PENDING
        )

        return productDao.updateProductPrice(updatedProduct) > 0
    }
}