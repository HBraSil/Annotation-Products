package com.example.anotafacil.domain.usecase

import com.example.anotafacil.data.network.RemoteDatabase
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.UserRole
import com.example.anotafacil.domain.model.toEntity
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.UserRepository
import jakarta.inject.Inject

class NewPurchaseUseCase @Inject constructor(
    private val remoteDb: RemoteDatabase,
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository
) {

    suspend operator fun invoke(
        purchase: Purchase
    ): Result<Unit> {

        val remoteUser = remoteDb.getUserData { uid ->
            val currentUser = userRepository.getCurrentUser(uid).getOrNull()

            currentUser?.let { return@getUserData it.toEntity() }
        }.getOrElse {
            return Result.failure(it)
        }

        val ownerUid = when (remoteUser.role) {
            UserRole.OWNER -> remoteUser.uid
            UserRole.SELLER -> remoteUser.ownerId
        }

        if (ownerUid == null) {
            return Result.failure(Exception("Você não está mais conectado a um proprietário"))
        }


        val purchaseWithOwner = purchase.copy(
            ownerId = ownerUid
        )

        return customerRepository.newPurchase(purchaseWithOwner)
    }
}