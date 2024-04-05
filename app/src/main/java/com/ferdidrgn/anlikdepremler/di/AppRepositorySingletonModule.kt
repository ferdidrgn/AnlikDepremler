package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.data.api.EarthquakeApi
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepositoryOlder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRepositorySingletonModule {

    //Example function
    //Different version of repository inject function
    @Singleton
    @Provides
    fun provideEarthquakeRepository(api: EarthquakeApi) = EarthquakeRepositoryOlder(api)
}