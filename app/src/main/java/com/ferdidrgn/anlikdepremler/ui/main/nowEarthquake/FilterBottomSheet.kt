package com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseBottomSheet
import com.ferdidrgn.anlikdepremler.databinding.BottomSheetFilterBinding
import com.ferdidrgn.anlikdepremler.model.dummyModel.EarthquakeBodyRequest
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.LAT_LAT
import com.ferdidrgn.anlikdepremler.tools.LAT_LONG
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.ferdidrgn.anlikdepremler.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheet(
    val onBtnApply: (LatLng?) -> Unit
) : BaseBottomSheet<MainViewModel, BottomSheetFilterBinding>(), OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var latLong: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.bottomSheetDialog)
    }

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()

    override fun getDataBinding(): BottomSheetFilterBinding =
        BottomSheetFilterBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {
        binding.viewModel = viewModel

        dialog?.setOnShowListener {
            val bottomSheet =
                dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapBottomSheet) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        registerLauncher()
        observe()

    }

    private fun observe() {
        with(viewModel) {
            clickClose.observe(this@FilterBottomSheet) {
                dismiss()
            }

            clickFilterClear.observe(this@FilterBottomSheet) {
                if (it) {
                    earthquakeBodyRequest = EarthquakeBodyRequest()
                    earthquakeBodyRequest.apply {
                        userLat = null
                        userLong = null
                    }
                    ml.value = ""
                    startDate.value = ""
                    endDate.value = ""
                    mMap.clear()
                }
            }

            clickApply.observe(this@FilterBottomSheet) {
                // viewModel.getFilters()
                onBtnApply(latLong)
                dismiss()
            }
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
        viewModel.earthquakeBodyRequest.userLat = latLng.latitude
        viewModel.earthquakeBodyRequest.userLong = latLng.longitude
        latLong = latLng
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        //locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        //locationListener = LocationListener { location ->
        ClientPreferences.inst.apply {
            val exampleLocation =
                if (userLat != null && userLong != null && userLat?.toDouble() != 0.0 && userLong?.toDouble() != 0.0) {
                    LatLng(userLat!!.toDouble(), userLong!!.toDouble())
                } else LatLng(LAT_LAT, LAT_LONG)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(exampleLocation, 15f))
        }

        isThereBeforeLatLong()
    }

    private fun isThereBeforeLatLong() {
        mMap.clear()
        with(viewModel.earthquakeBodyRequest) {
            if (this.userLat != null && this.userLong != null && userLat != 0.0 && userLong != 0.0) {
                mMap.addMarker(MarkerOptions().position(LatLng(userLat!!, userLong!!)))
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(userLat!!, userLong!!), 15f)
                )
                latLong = LatLng(userLat!!, userLong!!)
            }
        }
    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(RequestPermission()) { result ->
            if (result) {
                //permission granted
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, locationListener
                    )
                    val lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null) {
                        val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                    }
                }
            } else {
                //permission denied
                showToast(getString(R.string.permission_denied_location))
            }
        }
    }
}