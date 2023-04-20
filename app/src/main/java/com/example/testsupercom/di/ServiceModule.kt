package com.example.testsupercom.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.testsupercom.room.LocationDao
import com.example.testsupercom.services.MainRepository
import com.example.testsupercom.services.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @Provides
    @ServiceScoped
    fun provideMainViewModel(context: Context,repository: MainRepository): MainViewModel {
        return MainViewModel(context,repository)
    }
}






