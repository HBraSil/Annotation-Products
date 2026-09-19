package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.SyncStatus
import kotlin.uuid.Uuid

@Entity(
    tableName = "customer",
    foreignKeys = [
        ForeignKey(
            entity = CityEntity::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CustomerEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String = "",
    val lastPurchaseDate: String?,
    val owes: Double?,
    val extraInfo: String?,
    val cityId: Uuid,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

fun CustomerEntity.toDomain() = Customer(
    id = id,
    name = name,
    lastPurchaseDate = lastPurchaseDate,
    owes = owes,
    extraInfo = extraInfo,
    cityId = cityId
)
