package com.ferdidrgn.anlikdepremler.domain.repository

import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface EarthquakeRepository {
    fun getEarthquakes(): Flow<Resource<ArrayList<Earthquake>?>>

    fun getLocationEarthquakeList(city: String): Flow<Resource<ArrayList<Earthquake>?>>

    fun getTopTenEarthquakeList(): Flow<Resource<ArrayList<Earthquake>?>>

    fun getTopTenLocationEarthquakeList(
        city: String, limit: Int = 10
    ): Flow<Resource<ArrayList<Earthquake>?>>

    fun getOnlyDateEarthquakeList(date: String): Flow<Resource<ArrayList<Earthquake>?>>

    fun getDateBetweenEarthquakeList(
        startDate: String, endDate: String
    ): Flow<Resource<ArrayList<Earthquake>?>>
}