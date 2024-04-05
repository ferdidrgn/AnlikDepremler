package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDateBetweenEarthquakeUseCaseImpl @Inject constructor(
    private val repository: EarthquakeRepository
) : GetDateBetweenEarthquakeUseCase {
    override fun invoke(startDate: String, endDate: String):
            Flow<Resource<ArrayList<Earthquake>?>> =
        repository.getDateBetweenEarthquakeList(startDate, endDate)
}