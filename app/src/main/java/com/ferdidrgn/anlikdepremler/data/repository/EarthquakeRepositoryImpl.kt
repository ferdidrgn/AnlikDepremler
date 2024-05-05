package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdidrgn.anlikdepremler.util.base.BaseRepo
import com.ferdidrgn.anlikdepremler.util.base.Resource
import com.ferdidrgn.anlikdepremler.data.remote.api.EarthquakeApi
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.repository.EarthquakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EarthquakeRepositoryImpl @Inject constructor(
    private val earthquakeApi: EarthquakeApi
) : EarthquakeRepository, BaseRepo() {

    override fun getEarthquakes(): Flow<Resource<ArrayList<Earthquake>?>> =
        flowSafeApiCall { earthquakeApi.getEarthquakes() }

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