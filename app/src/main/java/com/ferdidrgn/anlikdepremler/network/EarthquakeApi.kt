package com.ferdidrgn.anlikdepremler.network

import com.ferdidrgn.anlikdepremler.model.Earthquake
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EarthquakeApi {

    @GET("kandilli/")
    suspend fun getEarthquake(): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/limit/10")
    suspend fun getTopTenEarthquakeList(): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/location/{city}/limit/{limit}")
    suspend fun getTopTenLocationEarthquakeList(
        @Path("city") city: String,
        @Path("limit") limit: Int
    ): Response<ArrayList<Earthquake>>
}