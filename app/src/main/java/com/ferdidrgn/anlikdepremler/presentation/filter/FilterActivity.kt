package com.ferdidrgn.anlikdepremler.presentation.filter

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.ActivityFilterBinding
import com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake.NowEarthquakeAdapter
import com.ferdidrgn.anlikdepremler.util.helpers.ClientPreferences
import com.ferdidrgn.anlikdepremler.util.helpers.LAT_LAT
import com.ferdidrgn.anlikdepremler.util.helpers.LAT_LONG
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope
import com.ferdidrgn.anlikdepremler.util.helpers.showToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tayfuncesur.curvedbottomsheet.CurvedBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterActivity : BaseActivity<FilterViewModel, ActivityFilterBinding>(), OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener {

    private lateinit var gMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var latLng: LatLng? = null

    override fun getVM(): Lazy<FilterViewModel> = viewModels()

    override fun getDataBinding(): ActivityFilterBinding =
        ActivityFilterBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {
        binding.viewModel = viewModel
        binding.nowEarthquakeAdapter = NowEarthquakeAdapter(viewModel, false)
        binding.customToolbar.backIconOnBackPress(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapBottomSheet) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        adsSet()
        bottomSheetInit()
        registerLauncher()
        observe()
    }

    private fun bottomSheetInit() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        CurvedBottomSheet(
            radius = (displayMetrics.widthPixels / 6).toFloat(),
            view = binding.bottomSheet,
            location = CurvedBottomSheet.Location.TOP
        ).init()
    }

    private fun observe() {

        viewModel.clickFilterClear.observe(this) { clear ->
            if (clear) {
                viewModel.clearXmlData()
                OnMapReadyCallback { googleMap ->
                    gMap = googleMap
                    gMap.clear()
                }
            }
        }

        viewModel.getNowEarthquakeList.observe(this) {
            scrollToTop()
        }

        viewModel.clickCstmDatePickerStartDate.observe(this) {
            binding.cdpStartDate.setCustomDataPickerClick()
        }

        viewModel.clickCstmDatePickerEndDate.observe(this) {
            binding.cdpEndDate.setCustomDataPickerClick()
        }

    }

    override fun onMapLongClick(latLng: LatLng) {
        mainScope {
            gMap.clear()
            gMap.addMarker(MarkerOptions().position(latLng))
            viewModel.userLat.emit(latLng.latitude)
            viewModel.userLong.emit(latLng.longitude)
            this@FilterActivity.latLng = latLng
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMapLongClickListener(this)

        //locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        //locationListener = LocationListener { location ->

        ClientPreferences.inst.apply {
            val exampleLocation =
                if (userLat != null && userLong != null && userLat?.toDouble() != 0.0 && userLong?.toDouble() != 0.0) {
                    LatLng(userLat!!.toDouble(), userLong!!.toDouble())
                } else LatLng(LAT_LAT, LAT_LONG)
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(exampleLocation, 15f))
        }

        isThereBeforeLatLong()
    }

    private fun isThereBeforeLatLong() {
        gMap.clear()
        with(viewModel) {
            if (userLat.value?.isNaN() == true && userLong.value?.isNaN() == true && userLat.value != 0.0 && userLong.value != 0.0) {
                gMap.addMarker(MarkerOptions().position(LatLng(userLat.value!!, userLong.value!!)))
                gMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(userLat.value!!, userLong.value!!), 15f
                    )
                )
                latLng = LatLng(userLat.value!!, userLong.value!!)
            }
        }
    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { result ->
            if (result) {
                //permission granted
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, locationListener
                    )
                    val lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null) {
                        val lastUserLocation =
                            LatLng(lastLocation.latitude, lastLocation.longitude)
                        gMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                lastUserLocation, 15f
                            )
                        )
                    }
                }
            } else {
                //permission denied
                showToast(getString(R.string.permission_denied_location))
            }
        }
    }

    private fun scrollToTop() {
        Handler(Looper.getMainLooper()).postDelayed({
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.scrollToPositionWithOffset(0, 0)
            binding.rvFilterEarthquake.layoutManager = linearLayoutManager
        }, 3000)
    }

    private fun adsSet() {
        setAds(binding.adViewList)
        setAds(binding.adViewFilter)
    }

    override fun onResume() {
        super.onResume()
        adsSet()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.getNowEarthquakeList.postValue(null)
        binding.unbind()
        networkMonitor.unregister()
    }

}