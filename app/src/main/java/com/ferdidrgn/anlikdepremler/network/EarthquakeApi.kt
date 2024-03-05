package com.ferdidrgn.anlikdepremler.network

import com.ferdidrgn.anlikdepremler.model.Earthquake
import retrofit2.Response
import retrofit2.http.GET

interface EarthquakeApi {

    @GET("kandilli/")
    suspend fun getEarthquake(): Response<ArrayList<Earthquake>>
}