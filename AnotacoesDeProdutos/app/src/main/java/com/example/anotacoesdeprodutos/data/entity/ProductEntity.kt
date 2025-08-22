package com.example.anotacoesdeprodutos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotacoesdeprodutos.domain.model.Product

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String = "",
    var price: Int = 0,
)

fun ProductEntity.toProductDomain() = Product(
    id = id,
    name = name,
    price = price
)