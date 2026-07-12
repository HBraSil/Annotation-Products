package com.example.anotacoesdeprodutos.domain.model

import com.example.anotacoesdeprodutos.data.entity.PaymentEntity

data class Payment(
    val id: Long = 0,
    val customerId: Long = 0,
    val paymentDate: Long = 0,
    val amount: Double = 0.0,
    val isTotalPayment: Boolean = false
)

fun Payment.toEntity() = PaymentEntity(
    id = id,
    customerId = customerId,
    paymentDate = paymentDate,
    amount = amount,
    isTotalPayment = isTotalPayment
)

