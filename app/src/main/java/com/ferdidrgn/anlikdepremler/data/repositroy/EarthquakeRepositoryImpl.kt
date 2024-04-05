package com.ferdidrgn.anlikdepremler.data.repositroy

import com.ferdidrgn.anlikdepremler.base.BaseRepo
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.data.api.EarthquakeApi
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EarthquakeRepositoryImpl @Inject constructor(
    private val earthquakeApi: EarthquakeApi
) : EarthquakeRepository, BaseRepo() {

    override fun getEarthquake(): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getEarthquake() }

    override fun getLocationEarthquakeList(city: String): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getLocationEarthquakeList(city) }

    override fun getTopTenEarthquakeList(): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getTopTenEarthquakeList() }

    override fun getTopTenLocationEarthquakeList(
        city: String,
        limit: Int
    ): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getTopTenLocationEarthquakeList(city, limit) }

    override fun getOnlyDateEarthquakeList(date: String): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getOnlyDateEarthquakeList(date) }

    override fun getDateBetweenEarthquakeList(
        startDate: String,
        endDate: String
    ): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getDateBetweenEarthquakeList(startDate, endDate) }

}