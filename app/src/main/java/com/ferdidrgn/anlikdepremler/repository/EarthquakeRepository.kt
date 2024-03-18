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

    suspend fun getTopTenEarthquakeList(): Resource<ArrayList<Earthquake>?> = safeApiCall {
        api.getTopTenEarthquakeList()
    }
}