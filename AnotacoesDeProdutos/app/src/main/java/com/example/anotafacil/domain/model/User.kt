package com.example.anotafacil.domain.model


data class User(
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.SELLER,
    val ownerId: Long? = null
)

enum class UserRole {
    OWNER,
    SELLER
}