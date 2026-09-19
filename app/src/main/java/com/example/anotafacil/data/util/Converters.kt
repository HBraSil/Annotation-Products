package com.example.anotafacil.data.util

import androidx.room.TypeConverter
import com.example.anotafacil.data.entity.PurchaseEntity
import com.example.anotafacil.domain.model.SyncStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.uuid.Uuid

class Converters {

    @TypeConverter
    fun fromUuid(uuid: Uuid): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUuid(value: String): Uuid {
        return Uuid.parse(value)
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

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}
