package com.example.anotafacil.di

import com.example.anotafacil.data.repository.AuthRepositoryImpl
import com.example.anotafacil.data.repository.CityRepositoryImpl
import com.example.anotafacil.data.repository.CustomerRepositoryImpl
import com.example.anotafacil.data.repository.ProductRepositoryImpl
import com.example.anotafacil.data.repository.SalesOverviewRepositoryImpl
import com.example.anotafacil.domain.repository.AuthRepository
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.ProductRepository
import com.example.anotafacil.domain.repository.SalesOverviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindSalesOverviewRepository(impl: SalesOverviewRepositoryImpl): SalesOverviewRepository
}