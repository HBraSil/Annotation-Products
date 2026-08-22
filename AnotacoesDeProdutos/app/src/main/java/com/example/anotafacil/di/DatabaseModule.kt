package com.example.anotafacil.di

import android.content.Context
import androidx.room.Room
import com.example.anotafacil.data.dao.CityDao
import com.example.anotafacil.data.dao.CustomerDao
import com.example.anotafacil.data.dao.ProductDao
import com.example.anotafacil.data.dao.PurchaseDao
import com.example.anotafacil.data.network.AppDatabase
import com.example.anotafacil.data.network.DatabaseCallback
import com.example.anotafacil.data.repository.CityRepositoryImpl
import com.example.anotafacil.data.repository.CustomerRepositoryImpl
import com.example.anotafacil.data.repository.ProductRepositoryImpl
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.ProductRepository
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
    fun provideDatabase(@ApplicationContext context: Context, productDaoProvider: Provider<ProductDao>, scope: CoroutineScope) = Room.databaseBuilder(
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
        .build()

    @Provides
    fun provideCityDao(database: AppDatabase) = database.cityDao()

    @Provides
    fun provideProductDao(database: AppDatabase) = database.productDao()

    @Provides
    fun providePurchaseDao(database: AppDatabase) = database.purchaseDao()

    @Provides
    fun provideCustomerDao(database: AppDatabase) = database.customerDao()

    @Provides
    fun provideCityRepository(cityDao: CityDao): CityRepository =
        CityRepositoryImpl(cityDao)

    @Provides
    fun provideCustomerRepository(customerDao: CustomerDao, purchaseDao: PurchaseDao, appDatabase: AppDatabase): CustomerRepository =
        CustomerRepositoryImpl(customerDao, purchaseDao, appDatabase)

    @Provides
    fun provideProductRepository(productDao: ProductDao): ProductRepository =
        ProductRepositoryImpl(productDao)
}