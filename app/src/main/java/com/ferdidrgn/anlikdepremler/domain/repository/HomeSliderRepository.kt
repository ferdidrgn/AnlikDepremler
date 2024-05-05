package com.ferdidrgn.anlikdepremler.domain.repository

import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData
import kotlinx.coroutines.flow.Flow

interface HomeSliderRepository {

    fun createExampleHomeSliderList(): Flow<List<HomeSliderData>>
}