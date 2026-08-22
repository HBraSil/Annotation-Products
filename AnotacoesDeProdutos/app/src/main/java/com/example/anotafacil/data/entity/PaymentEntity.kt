package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.Payment

@Entity(tableName = "payment")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val paymentDate: Long,
    val amount: Double,
    val isTotalPayment: Boolean = false
)

fun PaymentEntity.toDomain() = Payment(
    id = id,
    customerId = customerId,
    paymentDate = paymentDate,
    amount = amount,
    isTotalPayment = isTotalPayment
)
