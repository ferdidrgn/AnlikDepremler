package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.domain.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.domain.GetContactUsEmailUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.GetEarthquakeUseCaseImpl
import com.ferdidrgn.anlikdepremler.domain.GetTermsConditionOrPrivacyPolicyUseCase
import com.ferdidrgn.anlikdepremler.domain.GetTermsConditionOrPrivacyPolicyUseCaseImpl
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
}