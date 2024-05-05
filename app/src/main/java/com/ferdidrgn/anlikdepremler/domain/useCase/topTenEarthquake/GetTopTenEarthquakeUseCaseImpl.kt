package com.ferdidrgn.anlikdepremler.domain.useCase.topTenEarthquake

import com.ferdidrgn.anlikdepremler.util.base.Resource
import com.ferdidrgn.anlikdepremler.domain.repository.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopTenEarthquakeUseCaseImpl @Inject constructor(private val earthquakeRepository: EarthquakeRepository) :
    GetTopTenEarthquakeUseCase {
    override fun invoke(): Flow<Resource<ArrayList<Earthquake>?>> = earthquakeRepository.getTopTenEarthquakeList()
}