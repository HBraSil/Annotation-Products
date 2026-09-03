package com.example.anotafacil.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anotafacil.domain.model.Payment
import kotlin.uuid.Uuid

@Entity(tableName = "payment")
data class PaymentEntity(
    @PrimaryKey
    val id: Uuid,
    val customerId: Uuid,
    val paymentDate: Long,
    val amount: Double,
    val isTotalPayment: Boolean
)

fun PaymentEntity.toDomain() = Payment(
    id = id,
    customerId = customerId,
    paymentDate = paymentDate,
    amount = amount,
    isTotalPayment = isTotalPayment
)
