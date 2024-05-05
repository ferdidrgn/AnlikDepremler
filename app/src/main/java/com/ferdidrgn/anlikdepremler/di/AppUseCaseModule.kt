package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.domain.useCase.contactUsEmail.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.contactUsEmail.GetContactUsEmailUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.dataBetweenEarthquake.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.dataBetweenEarthquake.GetDateBetweenEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.earthquakes.GetEarthquakesUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.earthquakes.GetEarthquakesUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.homeSliderExample.GetExampleHomeSliderUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.homeSliderExample.GetExampleHomeSliderUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.locationEarthquake.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.locationEarthquake.GetLocationEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.dataOnlyEarthquake.GetOnlyDateEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.dataOnlyEarthquake.GetOnlyDateEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.termsConditionPrivacyPolicy.GetTermsConditionOrPrivacyPolicyUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.termsConditionPrivacyPolicy.GetTermsConditionOrPrivacyPolicyUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.topTenEarthquake.GetTopTenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.topTenEarthquake.GetTopTenEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.useCase.locationTopTenEarthquake.GetTopTenLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.locationTopTenEarthquake.GetTopTenLocationEarthquakeUseCaseImpl
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
    abstract fun bindGetEarthquakeUseCase(getDrinksWithFirstLetterUseCaseImpl: GetEarthquakesUseCaseImpl): GetEarthquakesUseCase

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