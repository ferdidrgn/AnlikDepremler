package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetOnlyDateEarthquakeUseCase {
    operator fun invoke(date: String): Flow<Resource<ArrayList<Earthquake>?>>
}