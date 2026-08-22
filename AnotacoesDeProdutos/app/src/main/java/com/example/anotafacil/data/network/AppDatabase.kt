package com.example.anotafacil.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.data.entity.CartItemEntity
import com.example.anotafacil.data.entity.PurchaseEntity
import com.example.anotafacil.data.entity.CityEntity
import com.example.anotafacil.data.entity.CustomerEntity
import com.example.anotafacil.data.entity.PaymentEntity
import com.example.anotafacil.data.entity.ProductEntity
import com.example.anotafacil.data.util.Converters

@Database(
    entities = [CityEntity::class, ProductEntity::class, CustomerEntity::class, PurchaseEntity::class, CartItemEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun purchaseDao(): PurchaseDao

}