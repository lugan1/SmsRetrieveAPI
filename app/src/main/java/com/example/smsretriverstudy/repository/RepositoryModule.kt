package com.example.smsretriverstudy.repository

import com.example.smsretriverstudy.data.source.AuthService
import com.example.smsretriverstudy.repository.impl.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(authService: AuthService): AuthRepository
        = AuthRepositoryImpl(authService)
}