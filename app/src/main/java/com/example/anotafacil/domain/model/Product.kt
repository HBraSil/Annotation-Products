package com.example.anotafacil.domain.model

import kotlin.uuid.Uuid


data class Product(
    val id: Uuid = Uuid.random(),
    val name: String = "",
    val price: Int = 0,
)