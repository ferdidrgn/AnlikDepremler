package com.ferdidrgn.anlikdepremler.domain.model.dummyModel

class FilterBodyRequest(
    var startDate: String = "",
    var endDate: String = "",
    var ml: String = "",
    var search: String = "",
    var chooseLat: Double? = null,
    var chooseLong: Double? = null,
)