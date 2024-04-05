package com.ferdidrgn.anlikdepremler.domain.model

import java.io.Serializable
import com.ferdidrgn.anlikdepremler.base.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class Earthquake(
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("depth")
    val depth: String? = null, //Derinlik
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("latitude")
    val latitude: String? = null,
    @SerializedName("longitude")
    val longitude: String? = null,
    @SerializedName("md")
    val md: String? = null, // Süreye Bağlı Büyüklük
    @SerializedName("ml")
    val ml: String? = null, //Büyüklük
    @SerializedName("mw")
    val mw: String? = null, // Moment Büyüklüğü
    @SerializedName("revize")
    val revize: String? = null, //İlkel ölçümden sonra ölçüm işlemi tekrar düzenli şekilde ölçüldü.
    @SerializedName("time")
    val time: String? = null,
    override val mId: Long = 1L
) : ListAdapterItem, Serializable