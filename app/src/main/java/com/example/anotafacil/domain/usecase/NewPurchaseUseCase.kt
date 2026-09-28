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

        val ownerResult = userRepository.getCurrentOwner()

        val owner = ownerResult.getOrElse {
            return Result.failure(Exception(it.message))
        }

        val purchaseWithOwner = purchase.copy(
            ownerId = owner.ownerId ?: return Result.failure(Exception("Proprietário não encontrado"))
        )

        return customerRepository.newPurchase(purchaseWithOwner)
    }
}