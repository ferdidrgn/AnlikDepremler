package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.data.repositroy.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationEarthquakeUseCaseImpl @Inject constructor(private val earthquakeRepository: EarthquakeRepository) :
    GetLocationEarthquakeUseCase {
    override fun invoke(city: String): Flow<Resource<ArrayList<Earthquake>?>> = earthquakeRepository.getLocationEarthquakeList(city)
}