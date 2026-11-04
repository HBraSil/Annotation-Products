package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.ProductEntity
import kotlin.uuid.Uuid


data class Product(
    val id: Uuid = Uuid.random(),
    val name: String = "",
    val price: Int = 0,
    val ownerId: String = "",
)

fun Product.toProductEntity() = ProductEntity(
    id = id,
    name = name,
    price = price,
    ownerId = ownerId
)