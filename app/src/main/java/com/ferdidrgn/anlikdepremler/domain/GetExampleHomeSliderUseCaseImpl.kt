package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.data.repositroy.HomeSliderRepository
import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExampleHomeSliderUseCaseImpl @Inject constructor(
    private val homeSliderRepository: HomeSliderRepository,
) : GetExampleHomeSliderUseCase {

    override operator fun invoke(): Flow<List<HomeSliderData>> =
        homeSliderRepository.createExampleHomeSliderList()
}