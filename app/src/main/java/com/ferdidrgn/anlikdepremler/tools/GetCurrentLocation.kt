package com.ferdidrgn.anlikdepremler.tools

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

interface locationListener {
    fun locationResponse(locationResult: LocationResult)
}

class GetCurrentLocation(var activity: AppCompatActivity, locationListener: locationListener) {
    private val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private val REQUEST_CODE_LOCATION = 100

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var locationRequest: LocationRequest? = null
    private var callbabck: LocationCallback? = null

    init {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity.applicationContext)

        inicializeLocationRequest()
        callbabck = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                locationListener.locationResponse(p0)
            }
        }
    }

    private fun inicializeLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 30000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun inicializeLocation() {
        if (validatePermissionsLocation()) {
            getLocation()
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        validatePermissionsLocation()
        locationRequest?.let { request ->
            callbabck?.let { call ->
                fusedLocationClient?.requestLocationUpdates(request, call, null)
            }
        }
    }

    private fun validatePermissionsLocation(): Boolean {
        val fineLocationAvailable = ActivityCompat.checkSelfPermission(
            activity.applicationContext,
            permissionFineLocation
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationAvailable = ActivityCompat.checkSelfPermission(
            activity.applicationContext,
            permissionCoarseLocation
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationAvailable && coarseLocationAvailable
    }

    private fun requestPermissions() {
        val contextProvider =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionFineLocation)

        if (contextProvider) {
            showToast("Permission is required to obtain location")
        }
        permissionRequest()
    }

    private fun permissionRequest() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permissionFineLocation, permissionCoarseLocation),
            REQUEST_CODE_LOCATION
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    showToast("You did not give permissions to get location")
                }
            }
        }
    }

    fun stopUpdateLocation() {
        callbabck?.let { this.fusedLocationClient?.removeLocationUpdates(it) }
    }
}