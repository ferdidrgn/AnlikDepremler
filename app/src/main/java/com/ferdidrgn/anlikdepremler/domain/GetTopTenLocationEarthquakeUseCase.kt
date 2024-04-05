package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetTopTenLocationEarthquakeUseCase {
    operator fun invoke(city: String, limit: Int = 10): Flow<Resource<ArrayList<Earthquake>?>>
}