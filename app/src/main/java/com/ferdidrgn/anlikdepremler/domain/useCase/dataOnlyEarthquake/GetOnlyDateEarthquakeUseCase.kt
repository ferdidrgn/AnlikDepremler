package com.ferdidrgn.anlikdepremler.domain.useCase.dataOnlyEarthquake

import com.ferdidrgn.anlikdepremler.util.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface GetOnlyDateEarthquakeUseCase {
    operator fun invoke(date: String): Flow<Resource<ArrayList<Earthquake>?>>
}