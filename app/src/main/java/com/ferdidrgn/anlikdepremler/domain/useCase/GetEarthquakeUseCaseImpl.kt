package com.ferdidrgn.anlikdepremler.domain.useCase

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEarthquakeUseCaseImpl @Inject constructor(
    private val earthquakeRepository: EarthquakeRepository,
) : GetEarthquakeUseCase {

    override operator fun invoke(): Flow<Resource<ArrayList<Earthquake>?>> =
        earthquakeRepository.getEarthquake()
}