package com.example.anotafacil.domain.model

import com.example.anotafacil.data.entity.PaymentEntity
import kotlin.uuid.Uuid

data class Payment(
    val id: Uuid = Uuid.random(),
    val customerId: Uuid? = null,
    val paymentDate: Long = 0,
    val amount: Double = 0.0,
    val isTotalPayment: Boolean = false
)

fun Payment.toEntity() = PaymentEntity(
    id = id,
    customerId = customerId ?: Uuid.random(),
    paymentDate = paymentDate,
    amount = amount,
    isTotalPayment = isTotalPayment
)

