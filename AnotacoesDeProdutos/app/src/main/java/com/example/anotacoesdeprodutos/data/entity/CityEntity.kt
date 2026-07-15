package com.example.anotacoesdeprodutos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotacoesdeprodutos.domain.model.City

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val customerCount: Int = 0
)


fun CityEntity.toCity() = City(
    id = id,
    name = name,
    customerCount = customerCount
)