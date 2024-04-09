package com.ferdidrgn.anlikdepremler.domain.useCase

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnlyDateEarthquakeUseCaseImpl @Inject constructor(
    private val repository: EarthquakeRepository
) : GetOnlyDateEarthquakeUseCase {
    override fun invoke(date: String): Flow<Resource<ArrayList<Earthquake>?>> =
        repository.getOnlyDateEarthquakeList(date)
}