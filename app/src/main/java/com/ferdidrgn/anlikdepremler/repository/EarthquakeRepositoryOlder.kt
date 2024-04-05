package com.ferdidrgn.anlikdepremler.repository

import com.ferdidrgn.anlikdepremler.base.BaseRepo
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.data.api.EarthquakeApi
import javax.inject.Inject

class EarthquakeRepositoryOlder @Inject constructor(
    private val api: EarthquakeApi,
) : BaseRepo() {

    suspend fun getEarthquake(): Resource<ArrayList<Earthquake>?> = suspendSafeApiCall {
        api.getEarthquake()
    }

    suspend fun getLocationEarthquakeList(city: String): Resource<ArrayList<Earthquake>?> =
        suspendSafeApiCall { api.getLocationEarthquakeList(city) }

    suspend fun getTopTenEarthquakeList(): Resource<ArrayList<Earthquake>?> = suspendSafeApiCall {
        api.getTopTenEarthquakeList()
    }

    suspend fun getTopTenLocationEarthquakeList(
        city: String, limit: Int = 10
    ): Resource<ArrayList<Earthquake>?> = suspendSafeApiCall {
        api.getTopTenLocationEarthquakeList(city, limit)
    }

    suspend fun getOnlyDateEarthquakeList(date: String): Resource<ArrayList<Earthquake>?> =
        suspendSafeApiCall {
            api.getOnlyDateEarthquakeList(date)
        }

    suspend fun getDateBetweenEarthquakeList(
        startDate: String, endDate: String
    ): Resource<ArrayList<Earthquake>?> = suspendSafeApiCall {
        api.getDateBetweenEarthquakeList(startDate, endDate)
    }
}