package com.example.testsupercom.di

import android.content.Context
import androidx.room.Room
import com.example.testsupercom.room.LocationDao
import com.example.testsupercom.room.MyDatabase
import com.example.testsupercom.services.MainRepository
import com.example.testsupercom.services.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getContext( @ApplicationContext context: Context) = context

    @Provides
    @Singleton
    fun provideDatabase(context: Context): MyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MyDatabase::class.java, "database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: MyDatabase): LocationDao {
        return database.userDao()
    }
}