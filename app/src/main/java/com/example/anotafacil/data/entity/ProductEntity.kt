package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.SyncStatus
import kotlin.uuid.Uuid

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    val id: Uuid = Uuid.random(),
    var name: String = "",
    var price: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

fun ProductEntity.toProductDomain() = Product(
    id = id,
    name = name,
    price = price
)