package com.ferdidrgn.anlikdepremler.domain.useCase

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.data.repository.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopTenLocationEarthquakeUseCaseImpl @Inject constructor(private val earthquakeRepository: EarthquakeRepository) :
    GetTopTenLocationEarthquakeUseCase {
    override fun invoke(city: String, limit: Int): Flow<Resource<ArrayList<Earthquake>?>> =
        earthquakeRepository.getTopTenLocationEarthquakeList(city, limit)
}