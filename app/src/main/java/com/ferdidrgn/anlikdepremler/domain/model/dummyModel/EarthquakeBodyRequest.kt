package com.ferdidrgn.anlikdepremler.domain.model.dummyModel

class EarthquakeBodyRequest(
    var location: String = "",
    var ml: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var onlyDate: String = "",
    var userLat: Double? = null,
    var userLong: Double? = null,
)