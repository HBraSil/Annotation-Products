package com.example.anotafacil.domain.model

data class User(
    val id: Long = 0,
    val name: String = "",
    val role: UserRole = UserRole.SELLER,
    val ownerId: Long? = null
)

enum class UserRole {
    OWNER,
    SELLER
}