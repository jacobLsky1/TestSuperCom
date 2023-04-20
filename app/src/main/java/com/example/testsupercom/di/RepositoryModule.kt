package com.example.testsupercom.di

import com.example.testsupercom.room.LocationDao
import com.example.testsupercom.services.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideMainRepository(userDao: LocationDao): MainRepository {
        return MainRepository(userDao)
    }
}