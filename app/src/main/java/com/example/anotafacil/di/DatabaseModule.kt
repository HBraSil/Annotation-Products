package com.example.anotafacil.di

import android.content.Context
import androidx.room.Room
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.data.network.DatabaseCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, productDaoProvider: Provider<ProductDao>, scope: CoroutineScope): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addCallback(
                DatabaseCallback(
                    productDaoProvider = productDaoProvider,
                    scope = scope
                )
            )
            .fallbackToDestructiveMigration(false)
        .build()

    @Provides
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    fun provideCityDao(database: AppDatabase) = database.cityDao()

    @Provides
    fun provideProductDao(database: AppDatabase) = database.productDao()

    @Provides
    fun providePurchaseDao(database: AppDatabase) = database.purchaseDao()

    @Provides
    fun provideCustomerDao(database: AppDatabase) = database.customerDao()

    @Provides
    fun providePaymentDao(database: AppDatabase) = database.paymentDao()
}