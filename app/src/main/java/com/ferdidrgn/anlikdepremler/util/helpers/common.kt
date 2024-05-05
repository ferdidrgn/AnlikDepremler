package com.ferdidrgn.anlikdepremler.util.helpers

import com.ferdidrgn.anlikdepremler.BuildConfig
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.location.LocationManagerCompat
import com.ferdidrgn.anlikdepremler.util.base.Err
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun log(string: String) {
    if (BuildConfig.DEBUG) {
        Handler(Looper.getMainLooper()).post {
            Log.d("Anlık Depremler", string)
        }
    }
}

fun View.onClickThrottled(skipDurationMillis: Long = 750, action: () -> Unit) {
    var isEnabled = true
    this.setOnClickListener {
        if (isEnabled) {
            action()
            isEnabled = false
            Handler().postDelayed({ isEnabled = true }, skipDurationMillis)
        }
    }
}

fun cameDate(date: String?): Calendar {
    // came Date Operations
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val cameDate = Calendar.getInstance()
    cameDate.time = date?.let { dateFormat.parse(it) }!!
    return cameDate
}

fun changeStringToDate(date: String): Calendar {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val cameDate = Calendar.getInstance()
    cameDate.time = date.let { dateFormat.parse(it) }!!
    return cameDate
}

fun changeDataShameFormat(backendDate: String): String? {
    val backendDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date: Date? = backendDateFormat.parse(backendDate)

    val displayDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return date?.let { displayDateFormat.format(it) }
}

fun showToast(
    message: String,
    context: Context? = com.ferdidrgn.anlikdepremler.AnlikDepremlerApp.inst.applicationContext
) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun getContext(): Context {
    return com.ferdidrgn.anlikdepremler.AnlikDepremlerApp.inst.applicationContext
}

fun checkIfTokenDeleted(error: Err?) {
    if (error?.code == 405)
        ClientPreferences.inst.token = ""
}

fun builderADS(context: Context, view: AdView) {
    MobileAds.initialize(context)
    val adRequest = AdRequest.Builder().build()
    view.loadAd(adRequest)
    view.adListener = object : AdListener() {
        override fun onAdClicked() {
            // Code to be executed when the user clicks on an ad.
        }

        override fun onAdClosed() {
            // Code to be executed when the user is about to return
            // to the app after tapping on an ad.
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
            // Code to be executed when an ad request fails.
        }

        override fun onAdImpression() {
            // Code to be executed when an impression is recorded
            // for an ad.
        }

        override fun onAdLoaded() {
            // Code to be executed when an ad finishes loading.
        }

        override fun onAdOpened() {
            // Code to be executed when an ad opens an overlay that
            // covers the screen.
        }
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

fun enableLocation(
    reqiuerActivity: Activity,
    launch: ActivityResultLauncher<IntentSenderRequest>,
) {
    val locationRequest = LocationRequest.create()
    locationRequest.apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 30 * 1000.toLong()
        fastestInterval = 5 * 1000.toLong()
    }

    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    builder.setAlwaysShow(true)

    LocationServices.getSettingsClient(reqiuerActivity).checkLocationSettings(builder.build())
        .addOnCompleteListener { task ->
            try {
                val response: LocationSettingsResponse = task.getResult(ApiException::class.java)
                if (response.locationSettingsStates?.isGpsPresent == true) {
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest =
                            e.status.resolution?.let { panding ->
                                IntentSenderRequest.Builder(panding).build()
                            }
                        launch.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        e.localizedMessage?.let { showToast(it) }
                    }
                }
            }
        }
}
