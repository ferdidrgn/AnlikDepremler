package com.ferdidrgn.anlikdepremler.domain.model

import java.io.Serializable
import com.ferdidrgn.anlikdepremler.util.base.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class Earthquake(
    val date: String? = null,
    val time: String? = null,
    val location: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val depth: String? = null,
    val ml: String? = null,
    val revize: String? = null,
    override val mId: Long = 1L
) : ListAdapterItem, Serializable