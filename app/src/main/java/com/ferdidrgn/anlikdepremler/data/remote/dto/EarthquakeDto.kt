package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import java.io.Serializable
import com.ferdidrgn.anlikdepremler.util.base.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class EarthquakeDto(
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("latitude")
    val latitude: String? = null,
    @SerializedName("longitude")
    val longitude: String? = null,
    @SerializedName("depth")
    val depth: String? = null, //Derinlik
    @SerializedName("md")
    val md: String? = null, //Süreye Bağlı Büyüklük
    @SerializedName("ml")
    val ml: String? = null, //Büyüklük
    @SerializedName("mw")
    val mw: String? = null, //Moment Büyüklüğü
    @SerializedName("revize")
    val revize: String? = null, //İlkel ölçümden sonra ölçüm işlemi tekrar düzenli şekilde ölçüldü.
    override val mId: Long = 1L
) : ListAdapterItem, Serializable


//Mapper
fun EarthquakeDto.toEarthquake(): Earthquake {
    return Earthquake(
        date = date,
        depth = depth,
        location = location,
        latitude = latitude,
        longitude = longitude,
        ml = ml,
        revize = revize,
        time = time
    )
}