package com.example.anotafacil.di

import com.example.anotafacil.data.datastore.UserDataStoreImpl
import com.example.anotafacil.data.repository.AccountProfileProfileRepositoryImpl
import com.example.anotafacil.data.repository.AuthRepositoryImpl
import com.example.anotafacil.data.repository.CityRepositoryImpl
import com.example.anotafacil.data.repository.CustomerRepositoryImpl
import com.example.anotafacil.data.repository.OwnerCodeRepositoryImpl
import com.example.anotafacil.data.repository.ProductRepositoryImpl
import com.example.anotafacil.data.repository.SalesOverviewRepositoryImpl
import com.example.anotafacil.data.repository.UserRepositoryImpl
import com.example.anotafacil.domain.datastore.UserDataStore
import com.example.anotafacil.domain.repository.AccountProfileRepository
import com.example.anotafacil.domain.repository.AuthRepository
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.OwnerCodeRepository
import com.example.anotafacil.domain.repository.ProductRepository
import com.example.anotafacil.domain.repository.SalesOverviewRepository
import com.example.anotafacil.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindSalesOverviewRepository(impl: SalesOverviewRepositoryImpl): SalesOverviewRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindOwnerCodeRepository(impl: OwnerCodeRepositoryImpl): OwnerCodeRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountProfileProfileRepositoryImpl): AccountProfileRepository

    @Binds
    @Singleton
    abstract fun bindDataStore(impl: UserDataStoreImpl): UserDataStore
}