package com.example.anotacoesdeprodutos.data.network


import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.anotacoesdeprodutos.data.dao.ProductDao
import com.example.anotacoesdeprodutos.data.entity.ProductEntity
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(
    private val productDaoProvider: Provider<ProductDao>,
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        Log.d("DATABASE", "onCreate executou")

        scope.launch(Dispatchers.IO) {

            Log.d("DATABASE", "Inserindo produtos")

            val databaseList = listOf(
                ProductEntity(name = "Sabão", price = 0),
                ProductEntity(name = "Brilho", price = 0),
                ProductEntity(name = "Alvejante", price = 0),
                ProductEntity(name = "Amaciante", price = 0),
                ProductEntity(name = "Desinfetante", price = 0),
                ProductEntity(name = "Água Sanitária", price = 0),
                ProductEntity(name = "Metazil", price = 0),
                ProductEntity(name = "Detergente", price = 0),
                ProductEntity(name = "Sabão 2l", price = 0),
                ProductEntity(name = "Brilho 2l", price = 0),
                ProductEntity(name = "Alvejante 2l", price = 0),
                ProductEntity(name = "Amaciante 2l", price = 0),
                ProductEntity(name = "Desinfetante 2l", price = 0),
                ProductEntity(name = "Água Sanitária 2l", price = 0),
                ProductEntity(name = "Metazil 2l", price = 0),
                ProductEntity(name = "Detergente 2l", price = 0),
            )


            productDaoProvider.get().insertAll(databaseList)

            Log.d("DATABASE", "Produtos inseridos")
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys = ON;")
    }
}