package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EarthquakeApi {

    @GET("kandilli/")
    suspend fun getEarthquakes(): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/limit/10")
    suspend fun getTopTenEarthquakeList(): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/location/{city}")
    suspend fun getLocationEarthquakeList(
        @Path("city") city: String,
    ): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/location/{city}/limit/{limit}")
    suspend fun getTopTenLocationEarthquakeList(
        @Path("city") city: String,
        @Path("limit") limit: Int
    ): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/date/{date}")
    suspend fun getOnlyDateEarthquakeList(
        @Path("date") date: String
    ): Response<ArrayList<Earthquake>>

    @GET("kandilli/api/between/{startDate}/{endDate}")
    suspend fun getDateBetweenEarthquakeList(
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String
    ): Response<ArrayList<Earthquake>>
}