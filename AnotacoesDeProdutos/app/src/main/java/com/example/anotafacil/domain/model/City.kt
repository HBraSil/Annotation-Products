package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.CityEntity


data class City(
    val id: Long = 0,
    val name: String = "",
    val customerCount: Int = 0,
)

fun City.toCityEntity() = CityEntity(
    id = id,
    name = name
)