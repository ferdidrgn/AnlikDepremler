package com.ferdidrgn.anlikdepremler.domain.useCase

import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData
import kotlinx.coroutines.flow.Flow

interface GetExampleHomeSliderUseCase {
    operator fun invoke(): Flow<List<HomeSliderData>>
}