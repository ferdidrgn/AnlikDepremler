package com.ferdidrgn.anlikdepremler.model.dummyModel

class EarthquakeBodyRequest(
    var search: String = "",
    var ml: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var userLat: Double? = null,
    var userLong: Double? = null,
)