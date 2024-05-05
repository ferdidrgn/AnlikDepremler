package com.ferdidrgn.anlikdepremler.domain.useCase.homeSliderExample

import com.ferdidrgn.anlikdepremler.domain.repository.HomeSliderRepository
import com.ferdidrgn.anlikdepremler.data.remote.dto.HomeSliderDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExampleHomeSliderUseCaseImpl @Inject constructor(
    private val homeSliderRepository: HomeSliderRepository,
) : GetExampleHomeSliderUseCase {

    override operator fun invoke(): Flow<List<HomeSliderDto>> =
        homeSliderRepository.createExampleHomeSliderList()
}