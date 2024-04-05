package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class AppRepositoryModule2 {
    @Binds
    @ViewModelScoped
    abstract fun bindEarthquakeRepository(earthquakeRepositoryImpl: EarthquakeRepositoryImpl): EarthquakeRepository

}