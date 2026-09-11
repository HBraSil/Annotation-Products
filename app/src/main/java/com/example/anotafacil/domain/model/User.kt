package com.example.anotafacil.domain.model


data class User(
    val name: String = "",
    val email: String = "",
    val ownerId: String? = null,
    val role: UserRole = UserRole.SELLER ,
)

enum class UserRole {
    OWNER,
    SELLER
}