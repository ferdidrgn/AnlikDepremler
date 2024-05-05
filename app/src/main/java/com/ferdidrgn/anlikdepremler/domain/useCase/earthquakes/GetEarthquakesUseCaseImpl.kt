package com.ferdidrgn.anlikdepremler.domain.useCase.earthquakes

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.repository.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEarthquakesUseCaseImpl @Inject constructor(
    private val earthquakeRepository: EarthquakeRepository,
) : GetEarthquakesUseCase {

    override operator fun invoke(): Flow<Resource<ArrayList<Earthquake>?>> =
        earthquakeRepository.getEarthquakes()
}