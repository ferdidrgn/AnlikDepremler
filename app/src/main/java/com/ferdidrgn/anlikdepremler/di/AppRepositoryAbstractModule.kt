package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.data.repositroy.AppToolsFireBaseQueriesRepositoryImpl
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepositoryImpl
import com.ferdidrgn.anlikdepremler.data.repositroy.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.data.repositroy.HomeSliderRepository
import com.ferdidrgn.anlikdepremler.data.repositroy.HomeSliderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class AppRepositoryAbstractModule {
    @Binds
    @ViewModelScoped
    abstract fun bindEarthquakeRepository(earthquakeRepositoryImpl: EarthquakeRepositoryImpl): EarthquakeRepository

    @Binds
    @ViewModelScoped
    abstract fun bindAppToolsFireBaseQueriesRepository(appToolsFireBaseQueriesRepositoryImpl: AppToolsFireBaseQueriesRepositoryImpl): AppToolsFireBaseQueriesRepository

    @Binds
    @ViewModelScoped
    abstract fun bindHomeSliderRepositoryRepository(homeSliderRepositoryImpl: HomeSliderRepositoryImpl): HomeSliderRepository

}