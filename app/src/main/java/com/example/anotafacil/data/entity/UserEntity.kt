package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.model.UserRole

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val ownerId: String?,
    val role: UserRole
)


fun UserEntity.toDomain() = User(
    name = uid,
    email = uid,
    ownerId = ownerId,
    role = role
)