package com.example.anotafacil.domain.usecase

import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.UserRepository
import jakarta.inject.Inject

class NewPurchaseUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository
) {

    suspend operator fun invoke(
        purchase: Purchase
    ): Result<Unit> {

        val ownerIdResult = userRepository.getCurrentOwnerId()

        val ownerId = ownerIdResult.getOrElse {
            return Result.failure(Exception(it.message))
        }

        val purchaseWithOwner = purchase.copy(ownerId = ownerId)

        return customerRepository.newPurchase(purchaseWithOwner)
    }
}