package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.City
import kotlin.uuid.Uuid

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String,
    val customerCount: Int = 0
)


fun CityEntity.toCity() = City(
    id = id,
    name = name,
    customerCount = customerCount
)