package com.example.anotacoesdeprodutos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.anotacoesdeprodutos.domain.model.Customer

enum class PaymentStatus { DEVE, TUDO_PAGO }

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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    //val status: PaymentStatus? = null,
    val lastPurchaseDate: String? = null,
    val owes: Double? = null,
    val extraInfo: String? = null,
    val cityId: Long
)

fun CustomerEntity.toDomain() = Customer(
    id = id,
    name = name,
    //status = status,
    lastPurchaseDate = lastPurchaseDate,
    owes = owes,
    extraInfo = extraInfo,
    cityId = cityId
)
