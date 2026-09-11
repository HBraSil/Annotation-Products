package com.example.anotafacil.domain.model


data class OwnerCode(
    val ownerId: String = "",
    val code: String = "------",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L
)
