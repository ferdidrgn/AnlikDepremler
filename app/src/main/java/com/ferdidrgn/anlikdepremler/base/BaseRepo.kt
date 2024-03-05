package com.ferdidrgn.anlikdepremler.base

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepo {
    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiToBeCalled()
                if (response.isSuccessful) {
                    Resource.Success(data = response.body())
                } else {
                    val errorBody = response.errorBody()?.string()?.let { JSONObject(it) }
                    val errorBody2 = response.errorBody()?.string()
                    val message = errorBody?.getJSONObject("err")?.getString("message")
                    val error = Gson().fromJson(errorBody2, Err::class.java)
                    Resource.Error(
                        error = Err(
                            code = response.code(),
                            message = error?.message ?: "Something went wrong",
                            //errorDetail = error?.errorDetail
                        )
                    )
                }
            } catch (e: HttpException) {
                Resource.Error(error = Err(message = e.message ?: "Something went wrong"))
            } catch (e: IOException) {
                Resource.Error(error = Err(message = "Please check your network connection"))
            } catch (e: Exception) {
                Log.e("BE Error", "///Exception: ${e.message}")
                Resource.Error(error = Err(message = e.message))
            }
        }
    }
}