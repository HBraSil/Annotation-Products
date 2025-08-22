package com.example.anotacoesdeprodutos.domain.model

import com.example.anotacoesdeprodutos.data.entity.ProductEntity

data class Product(
    val id: Long = 0,
    val name: String = "",
    val price: Int = 0,
)

fun Product.toProductEntity() = ProductEntity(
    id = id,
    name = name,
    price = price
)