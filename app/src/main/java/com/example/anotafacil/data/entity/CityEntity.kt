package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.SyncStatus
import kotlin.uuid.Uuid

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String,
    val customerCount: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)


fun CityEntity.toCity() = City(
    id = id,
    name = name,
    customerCount = customerCount
)