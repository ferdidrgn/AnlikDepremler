package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.network.EarthquakeApi
import com.ferdidrgn.anlikdepremler.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRepositoryModule {

    @Singleton
    @Provides
    fun provideUserAppToolsFireBaseQueries() = AppToolsFireBaseQueries()

    @Singleton
    @Provides
    fun provideEarthquakeRepository(api: EarthquakeApi) = EarthquakeRepository(api)

    @Singleton
    @Provides
    fun provideHomeSliderRepository() = HomeSliderRepository()
}