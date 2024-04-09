package com.ferdidrgn.anlikdepremler.domain.useCase

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetDateBetweenEarthquakeUseCase {
    operator fun invoke(startDate: String, endDate: String): Flow<Resource<ArrayList<Earthquake>?>>
}