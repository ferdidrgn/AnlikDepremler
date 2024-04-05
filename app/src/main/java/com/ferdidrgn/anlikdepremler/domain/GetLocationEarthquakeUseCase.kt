package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetLocationEarthquakeUseCase {
    operator fun invoke(city: String): Flow<Resource<ArrayList<Earthquake>?>>
}