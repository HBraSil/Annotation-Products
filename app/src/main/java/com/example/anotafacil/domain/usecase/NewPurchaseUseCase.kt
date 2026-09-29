package com.example.anotafacil.domain.usecase

import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.UserRole
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

        val user = userRepository
            .getCurrentUser()
            .getOrElse {
                return Result.failure(Exception(it.message))
            }

        val resolvedOwnerId = when (user.role) {
            UserRole.OWNER -> user.uid // Se é o Dono, o ownerId é o próprio ID dele
            UserRole.SELLER -> user.ownerId // Se é Vendedor, pega o ID do Dono vinculado
        }

        if (resolvedOwnerId.isNullOrBlank()) {
            return Result.failure(Exception("Vendedor não possui um proprietário vinculado."))
        }

        val purchaseWithOwner = purchase.copy(
            ownerId = resolvedOwnerId
        )

        return customerRepository.newPurchase(purchaseWithOwner)
    }
}