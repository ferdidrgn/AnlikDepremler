package com.ferdidrgn.anlikdepremler.domain.repository

import com.ferdidrgn.anlikdepremler.data.remote.dto.HomeSliderDto
import kotlinx.coroutines.flow.Flow

interface HomeSliderRepository {

    fun createExampleHomeSliderList(): Flow<List<HomeSliderDto>>
}