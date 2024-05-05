package com.ferdidrgn.anlikdepremler.domain.useCase.topTenEarthquake

import com.ferdidrgn.anlikdepremler.util.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetTopTenEarthquakeUseCase {
    operator fun invoke(): Flow<Resource<ArrayList<Earthquake>?>>
}