package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.domain.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.domain.GetContactUsEmailUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetDateBetweenEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetExampleHomeSliderUseCase
import com.ferdidrgn.anlikdepremler.domain.GetExampleHomeSliderUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetLocationEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetOnlyDateEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetOnlyDateEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetTermsConditionOrPrivacyPolicyUseCase
import com.ferdidrgn.anlikdepremler.domain.GetTermsConditionOrPrivacyPolicyUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetTopTenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetTopTenEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetTopTenLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetTopTenLocationEarthquakeUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppUseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun bindGetEarthquakeUseCase(getDrinksWithFirstLetterUseCaseImpl: GetEarthquakeUseCaseImpl): GetEarthquakeUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetContactUsEmailUseCase(getContactUsEmailUseCaseImpl: GetContactUsEmailUseCaseImpl): GetContactUsEmailUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetTermsConditionOrPrivacyPolicyUseCase(
        getTermsConditionOrPrivacyPolicyUseCaseImpl: GetTermsConditionOrPrivacyPolicyUseCaseImpl
    ): GetTermsConditionOrPrivacyPolicyUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetExampleHomeSliderUseCase(getExampleHomeSliderUseCaseImpl: GetExampleHomeSliderUseCaseImpl): GetExampleHomeSliderUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetLocationEarthquakeUseCase(getLocationEarthquakeUseCaseImpl: GetLocationEarthquakeUseCaseImpl): GetLocationEarthquakeUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetTopTenEarthquakeUseCase(getTopTenEarthquakeUseCaseImpl: GetTopTenEarthquakeUseCaseImpl): GetTopTenEarthquakeUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetTopTenLocationEarthquakeUseCase(getTopTenLocationEarthquakeUseCaseImpl: GetTopTenLocationEarthquakeUseCaseImpl): GetTopTenLocationEarthquakeUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetOnlyDateEarthquakeUseCase(getOnlyDateEarthquakeUseCaseImpl: GetOnlyDateEarthquakeUseCaseImpl): GetOnlyDateEarthquakeUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetDateBetweenEarthquakeUseCase(getDateBetweenEarthquakeUseCaseImpl: GetDateBetweenEarthquakeUseCaseImpl): GetDateBetweenEarthquakeUseCase

}