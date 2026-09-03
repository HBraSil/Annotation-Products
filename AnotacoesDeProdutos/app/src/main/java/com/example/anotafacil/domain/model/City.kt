package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.CityEntity
import kotlin.uuid.Uuid


data class City(
    val id: Uuid = Uuid.random(),
    val name: String = "",
    val customerCount: Int = 0,
)

fun City.toCityEntity() = CityEntity(
    id = id,
    name = name
)