package com.ferdidrgn.anlikdepremler.tools

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

interface CurrentLocationListener {
    fun locationResponse(locationResult: LocationResult)
}
class LocationManager(private val context: Context, currentLocationListener: CurrentLocationListener) {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var callback: LocationCallback? = null

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        initializeLocationRequest()

        callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                currentLocationListener.locationResponse(p0)
            }
        }

        getLocation()
    }

    //Get Current Location
    private fun initializeLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 20000
        locationRequest?.fastestInterval = 3000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun stopUpdateLocation() {
        callback?.let { this.fusedLocationClient?.removeLocationUpdates(it) }
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationRequest?.let { request ->
            callback?.let { call ->
                fusedLocationClient?.requestLocationUpdates(request, call, null)
            }
        }
    }


    // Get Last Location
    fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback(null)
            return
        }

        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            callback(location)
        }?.addOnFailureListener { e ->
            // Handle failure
            showToast("Error getting last known location: ${e.message}")
            callback(null)
        }
    }
}