package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val uid: String = "",
    val name: String,
    val email: String,
    val ownerId: String?,
    val role: UserRole = UserRole.SELLER
)


fun UserEntity.toDomain() = User(
    uid = uid,
    name = name,
    email = email,
    ownerId = ownerId,
    role = role
)