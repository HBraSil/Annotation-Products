package com.example.anotafacil.data.network


import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.entity.ProductEntity
import com.example.anotafacil.domain.model.SyncStatus
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DatabaseCallback(
    private val productDaoProvider: Provider<ProductDao>,
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            val databaseList = listOf(
                ProductEntity(name = "Sabão", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Brilho", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Alvejante", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Amaciante", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Desinfetante", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Água Sanitária", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Metazil", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Detergente", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Sabão 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Brilho 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Alvejante 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Amaciante 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Desinfetante 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Água Sanitária 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Metazil 2l", price = 0, syncStatus = SyncStatus.SYNCED),
                ProductEntity(name = "Detergente 2l", price = 0, syncStatus = SyncStatus.SYNCED),
            )


            productDaoProvider.get().insertAll(databaseList)
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys = ON;")
    }
}