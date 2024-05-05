package com.ferdidrgn.anlikdepremler.domain.useCase.homeSliderExample

import com.ferdidrgn.anlikdepremler.data.remote.dto.HomeSliderDto
import kotlinx.coroutines.flow.Flow

interface GetExampleHomeSliderUseCase {
    operator fun invoke(): Flow<List<HomeSliderDto>>
}