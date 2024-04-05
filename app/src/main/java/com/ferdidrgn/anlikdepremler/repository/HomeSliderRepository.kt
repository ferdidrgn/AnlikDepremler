package com.ferdidrgn.anlikdepremler.repository

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData

class HomeSliderRepository {

    fun createExampleHomeSliderList(): List<HomeSliderData> {
        val homeSliderList = ArrayList<HomeSliderData>()

        val slider1 = HomeSliderData(
            image = "https://cdn.pixabay.com/photo/2018/02/20/13/46/earthquake-3167693_1280.jpg",
            title = "Geçmiş Olsun Türkiyem",
            description = "Description1",
            link = "https://www.google.com"
        )

        homeSliderList.add(slider1)

        val slider2 = HomeSliderData(
            image = "https://cdn.pixabay.com/photo/2016/09/12/21/58/earthquake-1665895_1280.jpg",
            title = "Geçmiş Olsun Türkiyem",
            description = "Description2",
            link = "https://www.google.com"
        )
        homeSliderList.add(slider2)

        val slider3 = HomeSliderData(
            image = "https://cdn.pixabay.com/photo/2016/09/12/21/57/earthquake-1665878_1280.jpg",
            title = "Geçmiş Olsun Türkiyem",
            link = "https://www.google.com"
        )
        homeSliderList.add(slider3)

        return homeSliderList
    }
}