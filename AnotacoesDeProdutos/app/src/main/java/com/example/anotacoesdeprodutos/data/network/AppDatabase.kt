package com.example.anotacoesdeprodutos.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.anotacoesdeprodutos.data.dao.CityDao
import com.example.anotacoesdeprodutos.data.dao.CustomerDao
import com.example.anotacoesdeprodutos.data.dao.ProductDao
import com.example.anotacoesdeprodutos.data.entity.CartItemEntity
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.data.entity.CityEntity
import com.example.anotacoesdeprodutos.data.entity.CustomerEntity
import com.example.anotacoesdeprodutos.data.entity.PaymentEntity
import com.example.anotacoesdeprodutos.data.entity.ProductEntity
import com.example.anotacoesdeprodutos.data.util.Converters

@Database(
    entities = [CityEntity::class, ProductEntity::class, CustomerEntity::class, PurchaseEntity::class, CartItemEntity::class, PaymentEntity::class],
    version = 25,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
}