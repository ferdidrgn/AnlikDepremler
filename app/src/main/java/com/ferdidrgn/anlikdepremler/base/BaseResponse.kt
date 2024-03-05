package com.ferdidrgn.anlikdepremler.base

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("data")
    val `data`: T,
    @SerializedName("err")
    val err: Err,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)

data class Err(
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("code")
    val code: Int? = null,
)