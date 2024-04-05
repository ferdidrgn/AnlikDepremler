package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData
import kotlinx.coroutines.flow.Flow

interface GetExampleHomeSliderUseCase {
    operator fun invoke(): Flow<List<HomeSliderData>>
}