package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdidrgn.anlikdepremler.data.remote.dto.HomeSliderDto
import com.ferdidrgn.anlikdepremler.domain.repository.HomeSliderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeSliderRepositoryImpl @Inject constructor() : HomeSliderRepository {

    override fun createExampleHomeSliderList(): Flow<List<HomeSliderDto>> = flow {
        val homeSliderList = ArrayList<HomeSliderDto>()

        val slider1 = HomeSliderDto(
            image = "https://cdn.pixabay.com/photo/2018/02/20/13/46/earthquake-3167693_1280.jpg",
            title = "Geçmiş Olsun Türkiyem",
            description = "Description1",
            link = "https://www.google.com"
        )

        homeSliderList.add(slider1)

        val slider2 = HomeSliderDto(
            image = "https://cdn.pixabay.com/photo/2016/09/12/21/58/earthquake-1665895_1280.jpg",
            title = "Geçmiş Olsun Türkiyem",
            description = "Description2",
            link = "https://www.google.com"
        )
        homeSliderList.add(slider2)

        val slider3 = HomeSliderDto(
            image = "https://cdn.pixabay.com/photo/2016/09/12/21/57/earthquake-1665878_1280.jpg",
            title = "Geçmiş Olsun Türkiyem",
            link = "https://www.google.com"
        )
        homeSliderList.add(slider3)

        emit(homeSliderList)
    }
}