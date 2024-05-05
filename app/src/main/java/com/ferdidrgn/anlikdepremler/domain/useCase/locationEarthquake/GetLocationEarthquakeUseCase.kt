package com.ferdidrgn.anlikdepremler.domain.useCase.locationEarthquake

import com.ferdidrgn.anlikdepremler.util.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetLocationEarthquakeUseCase {
    operator fun invoke(city: String): Flow<Resource<ArrayList<Earthquake>?>>
}