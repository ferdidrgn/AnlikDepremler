package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdidrgn.anlikdepremler.base.BaseRepo
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.data.remote.api.EarthquakeApi
import javax.inject.Inject

class EarthquakeRepositoryOlder @Inject constructor(
    private val api: EarthquakeApi,
) : BaseRepo() {

    //Example function
    //Older version of getEarthquake function
    suspend fun getEarthquakes(): Resource<ArrayList<Earthquake>?> = suspendSafeApiCall {
        api.getEarthquakes()
    }
}