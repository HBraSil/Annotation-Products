package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.firestore.FirestoreSchema


data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val ownerId: String? = null,
    val role: UserRole? = null,
) {
    val currentOwnerUid: String?
        get() = when(role) {
            UserRole.OWNER -> uid
            UserRole.SELLER -> ownerId
            else -> null
        }
}


fun User.toEntity() = UserEntity(
    uid = uid,
    name = name,
    email = email,
    ownerId = ownerId,
    role = role
)


enum class UserRole {
    OWNER,
    SELLER
}