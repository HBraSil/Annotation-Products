package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.UserEntity


data class User(
    val name: String = "",
    val email: String = "",
    val ownerId: String? = null,
    val role: UserRole = UserRole.SELLER ,
)


fun User.toEntity() = UserEntity(
    uid = name,
    email = email,
    ownerId = ownerId,
    role = role
)

enum class UserRole {
    OWNER,
    SELLER
}