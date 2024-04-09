package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.domain.useCase.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetContactUsEmailUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetDateBetweenEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetExampleHomeSliderUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetExampleHomeSliderUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetLocationEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetOnlyDateEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetOnlyDateEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTermsConditionOrPrivacyPolicyUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTermsConditionOrPrivacyPolicyUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTopTenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTopTenEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTopTenLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTopTenLocationEarthquakeUseCaseImpl
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