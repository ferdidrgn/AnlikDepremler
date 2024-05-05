package com.ferdidrgn.anlikdepremler.util.base

sealed class Resource<T>(
    val data: T? = null,
    val error: Err? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)

    class Error<T>(error: Err) : Resource<T>(error = error)

    class Loading<T>(loading:Boolean) : Resource<T>()
}