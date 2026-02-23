package com.example.zentrix.core.di

import com.example.zentrix.data.repository.AuthRepositoryImpl
import com.example.zentrix.data.repository.FirestoreRepository
import com.example.zentrix.data.repository.ProductRepositoryImpl
import com.example.zentrix.domain.repository.AuthRepository
import com.example.zentrix.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent :: class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

}
@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule{
    @Provides
    @Singleton
    fun provideFirestoreRepository(): FirestoreRepository {
        return FirestoreRepository()
    }
}