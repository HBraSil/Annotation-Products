package com.example.anotafacil.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.PaymentDao
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.data.dao.UserDao
import com.example.anotafacil.data.entity.CartItemEntity
import com.example.anotafacil.data.entity.PurchaseEntity
import com.example.anotafacil.data.entity.CityEntity
import com.example.anotafacil.data.entity.CustomerEntity
import com.example.anotafacil.data.entity.PaymentEntity
import com.example.anotafacil.data.entity.ProductEntity
import com.example.anotafacil.data.entity.UserEntity
import com.example.anotafacil.data.util.Converters
import com.example.anotafacil.domain.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

@Database(
    entities = [
        UserEntity::class,
        CityEntity::class,
        ProductEntity::class,
        CustomerEntity::class,
        PurchaseEntity::class,
        CartItemEntity::class,
        PaymentEntity::class
    ],
    version = 11,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cityDao(): CityDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun paymentDao(): PaymentDao


    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            clearAllTables()
        }
    }


    suspend fun clearSellerData(): Result<Boolean> {

        return withContext(Dispatchers.IO) {
            try {
                withTransaction {
                    paymentDao().deleteAll()
                    purchaseDao().deleteAll()
                    customerDao().deleteAll()
                    cityDao().deleteAll()
                    productDao().deleteAll()
                }

                Result.success(true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun hasPendingData(ownerId: String): Boolean {
        return withContext(Dispatchers.IO) {
              cityDao().getCitiesBySyncStatus(SyncStatus.PENDING, ownerId).isNotEmpty() ||
              productDao().getProductsBySyncStatus(SyncStatus.PENDING, ownerId).isNotEmpty() ||
              customerDao().getCustomersBySyncStatus(SyncStatus.PENDING).isNotEmpty() ||
              purchaseDao().getPurchasesBySyncStatus(SyncStatus.PENDING).isNotEmpty() ||
              paymentDao().getPaymentsBySyncStatus(SyncStatus.PENDING).isNotEmpty()
        }
    }
}