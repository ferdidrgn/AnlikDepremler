package com.ferdidrgn.anlikdepremler.repository

import com.ferdidrgn.anlikdepremler.base.BaseRepo
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.model.Earthquake
import com.ferdidrgn.anlikdepremler.network.EarthquakeApi
import javax.inject.Inject

class EarthquakeRepository @Inject constructor(
    private val api: EarthquakeApi,
) : BaseRepo() {

    suspend fun getEarthquake(): Resource<ArrayList<Earthquake>?> = safeApiCall {
        api.getEarthquake()
    }

    suspend fun getLocationEarthquakeList(city: String): Resource<ArrayList<Earthquake>?> =
        safeApiCall { api.getLocationEarthquakeList(city) }

    suspend fun getTopTenEarthquakeList(): Resource<ArrayList<Earthquake>?> = safeApiCall {
        api.getTopTenEarthquakeList()
    }

    suspend fun getTopTenLocationEarthquakeList(
        city: String, limit: Int = 10
    ): Resource<ArrayList<Earthquake>?> = safeApiCall {
        api.getTopTenLocationEarthquakeList(city, limit)
    }

    suspend fun getOnlyDateEarthquakeList(date: String): Resource<ArrayList<Earthquake>?> =
        safeApiCall {
            api.getOnlyDateEarthquakeList(date)
        }

    suspend fun getDateBetweenEarthquakeList(
        startDate: String, endDate: String
    ): Resource<ArrayList<Earthquake>?> = safeApiCall {
        api.getDateBetweenEarthquakeList(startDate, endDate)
    }
}