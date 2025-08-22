package com.example.anotacoesdeprodutos.domain.model

import com.example.anotacoesdeprodutos.data.entity.CityEntity


data class City(
    val id: Long = 0,
    val name: String = "",
    val customerCount: Int = 0,
    val lastSale: String? = null,
)

fun City.toCityEntity() = CityEntity(
    id = id,
    name = name,
    customerCount = customerCount,
    lastSale = lastSale,
)