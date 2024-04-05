package com.ferdidrgn.anlikdepremler.tools

import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.model.dummyModel.EarthquakeBodyRequest
import com.ferdidrgn.anlikdepremler.tools.cameDate
import com.ferdidrgn.anlikdepremler.tools.changeStringToDate
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.ferdidrgn.anlikdepremler.tools.timeBetweenWeek
import java.util.*
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.collections.ArrayList

private fun isNotEmpty(value: ArrayList<Earthquake>) = value.isNotEmpty()

fun checkMapManuelStatus(userLat: Double, userLong: Double): Boolean {
    return userLat != 0.0 && userLong != 0.0
}

fun checkBetweenData(startDate: String, endDate: String): Boolean {
    return startDate.isNotEmpty() && endDate.isNotEmpty()
}

fun getAllFilterQueriers(
    earthquakeBodyRequest: EarthquakeBodyRequest,
    getFilterEarthquakeList: ArrayList<Earthquake>,
    status: (ArrayList<Earthquake>?) -> Unit
) {
    var filterEarthquakeList: ArrayList<Earthquake>? = null

    earthquakeBodyRequest.apply {

        //konum, şiddet ve tarih
        if (checkMapManuelStatus(
                userLat ?: 0.0, userLong ?: 0.0
            ) &&
            checkBetweenData(
                startDate,
                endDate
            ) && ml.isNotEmpty()
        ) {
            filterEarthquakeList =
                getAllFilterCheck(
                    getFilterEarthquakeList,
                    earthquakeBodyRequest, true, true, true
                )
        }//konum ve tarih
        else if (checkMapManuelStatus(
                userLat ?: 0.0, userLong ?: 0.0
            ) &&
            checkMapManuelStatus(
                userLat ?: 0.0, userLong ?: 0.0
            ) && ml.isEmpty()
        ) {
            filterEarthquakeList =
                getAllFilterCheck(
                    getFilterEarthquakeList,
                    earthquakeBodyRequest, true, true, false
                )

        }//Konum ve Şiddet
        else if (checkMapManuelStatus(
                userLat ?: 0.0, userLong ?: 0.0
            ) &&
            ml.isNotEmpty() && !checkBetweenData(
                startDate,
                endDate
            )
        ) {
            filterEarthquakeList =
                getAllFilterCheck(
                    getFilterEarthquakeList,
                    earthquakeBodyRequest, true, false, true
                )
        } //Şiddet ve Tarih
        else if (ml.isNotEmpty() && checkBetweenData(
                startDate,
                endDate
            ) && !checkMapManuelStatus
                (userLat ?: 0.0, userLong ?: 0.0)
        ) {
            filterEarthquakeList = getAllFilterCheck(
                getFilterEarthquakeList,
                earthquakeBodyRequest, false, true, true
            )
        } //Konum
        else if (checkMapManuelStatus(userLat ?: 0.0, userLong ?: 0.0) &&
            ml.isEmpty() && !checkBetweenData(startDate, endDate)
        ) {
            filterEarthquakeList = getAllFilterCheck(
                getFilterEarthquakeList,
                earthquakeBodyRequest, true, false, false
            )
        } //Şiddet
        else if (ml.isNotEmpty() && !checkMapManuelStatus(userLat ?: 0.0, userLong ?: 0.0) &&
            !checkBetweenData(startDate, endDate)
        ) {
            filterEarthquakeList = getAllFilterCheck(
                getFilterEarthquakeList,
                earthquakeBodyRequest, false, false, true
            )
        } //Tarih
        else if (ml.isEmpty() && checkBetweenData(startDate, endDate) &&
            !checkMapManuelStatus(userLat ?: 0.0, userLong ?: 0.0)
        ) {
            filterEarthquakeList = getAllFilterCheck(
                getFilterEarthquakeList,
                earthquakeBodyRequest, false, true, false
            )
        }
    }

    return status.invoke(filterEarthquakeList)
}


fun getAllFilterCheck(
    getFilterEarthquakeList: ArrayList<Earthquake>,
    earthquakeBodyRequest: EarthquakeBodyRequest,
    locationFilter: Boolean,
    dateFilter: Boolean,
    mlFilter: Boolean
): ArrayList<Earthquake>? {
    val filterEarthquakeList = ArrayList<Earthquake>()
    val filterDate = ArrayList<Earthquake>()
    var returnFilterList: ArrayList<Earthquake> = ArrayList()

    if (locationFilter) {
        returnFilterList = getLocationFilterManuel(
            earthquakeBodyRequest.userLat!!,
            earthquakeBodyRequest.userLong!!,
            getFilterEarthquakeList
        )
    }

    if (mlFilter) {
        (if (isNotEmpty(returnFilterList)) returnFilterList else getFilterEarthquakeList).forEach { earthquake ->
            if (earthquake.ml.isNullOrEmpty().not())
                if (earthquake.ml?.toDoubleOrNull()!! > earthquakeBodyRequest.ml.toDoubleOrNull()!!)
                    filterEarthquakeList.add(earthquake)
        }
        returnFilterList = filterEarthquakeList
    }

    if (dateFilter) {
        val startDate = changeStringToDate(earthquakeBodyRequest.startDate)
        val endDate = changeStringToDate(earthquakeBodyRequest.endDate)

        filterDate.addAll(
            (if (isNotEmpty(returnFilterList)) returnFilterList else getFilterEarthquakeList).filter { data ->
                val dataDate = changeStringToDate(data.date.toString())
                dataDate in startDate..endDate
            })

        returnFilterList = filterDate
    }
    return returnFilterList
}

fun getLocationFilterManuel(
    lat: Double,
    long: Double,
    earthquakeList: ArrayList<Earthquake>
): ArrayList<Earthquake> {
    val returnFilterList = ArrayList<Earthquake>()

    val earthRadius = 6371.0 // Dünya'nın yarı çapı kilometre cinsinden

    // Radyan cinsinden enlem ve boylam değerleri
    val latRad = Math.toRadians(lat)

    // 120 km'lik alanı hesapla
    val distance = 60.0

    val deltaLat = distance / earthRadius
    val deltaLon = distance / (earthRadius * kotlin.math.cos(latRad))

    val minLat = lat - Math.toDegrees(deltaLat)
    val maxLat = lat + Math.toDegrees(deltaLat)
    val minLon = long - Math.toDegrees(deltaLon)
    val maxLon = long + Math.toDegrees(deltaLon)

    earthquakeList.forEach { earthquake ->
        if (earthquake.latitude?.toDouble()!! in minLat..maxLat && earthquake.longitude?.toDouble()!! in minLon..maxLon) {
            returnFilterList.add(earthquake)
        }
    }
    return returnFilterList
}

fun distanceOperations(
    itemLat: Double,
    litemLong: Double,
    userLat: Double,
    userLong: Double
): Double {
    //haversineDistance
    val r = 6371 // Yerel radyan, Dünya'nın yarıçapı (km)

    val dLat = Math.toRadians(userLat - itemLat)
    val dLon = Math.toRadians(userLong - litemLong)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(itemLat)) * kotlin.math.cos(Math.toRadians(userLat)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))

    return r * c

}

fun getFilterOneWeek(item: Earthquake?): ArrayList<Earthquake> {
    val filterWeekList = ArrayList<Earthquake>()

    val today = Calendar.getInstance()
    today.time = Date()

    // Time Between
    val startTimeForFilter = timeBetweenWeek()

    // came Date Operations
    val cameDate = cameDate(item?.date)

    // date between to control
    if (cameDate.after(startTimeForFilter) && cameDate.before(today)) {
        filterWeekList.add(item!!)
    }
    return filterWeekList
}