package com.example.anotacoesdeprodutos.data.util

import androidx.room.TypeConverter
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.data.entity.PaymentStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toPaymentStatus(status: String?): PaymentStatus? {
        return status?.let { PaymentStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromCartItemEntityList(value: List<PurchaseEntity>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<PurchaseEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCartItemEntityList(value: String?): List<PurchaseEntity>? {
        val gson = Gson()
        val type = object : TypeToken<List<PurchaseEntity>>() {}.type
        return gson.fromJson(value, type)
    }
}
