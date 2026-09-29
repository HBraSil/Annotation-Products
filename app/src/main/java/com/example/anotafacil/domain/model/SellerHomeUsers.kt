package com.example.anotafacil.domain.model

data class SellerHomeUsers(
    val seller: User = User(),
    val owner: User = User()
)