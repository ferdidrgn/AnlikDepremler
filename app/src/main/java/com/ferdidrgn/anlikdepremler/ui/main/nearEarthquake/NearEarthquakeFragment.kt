package com.ferdidrgn.anlikdepremler.ui.main.nearEarthquake

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentNearEarthquakeBinding
import com.ferdidrgn.anlikdepremler.tools.*
import com.ferdidrgn.anlikdepremler.tools.NavHandler
import com.ferdidrgn.anlikdepremler.ui.main.MainActivity
import com.ferdidrgn.anlikdepremler.ui.main.MainViewModel
import com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake.NowEarthquakeAdapter
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class NearEarthquakeFragment :
    BaseFragment<MainViewModel, FragmentNearEarthquakeBinding>() {

    private var job: Job? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var location: GetCurrentLocation? = null
    private var latLng: LatLng? = null

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentNearEarthquakeBinding =
        FragmentNearEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        builderADS(requireContext(), binding.adView)

        binding.includeEarthquakeList.apply {
            viewModel = this@NearEarthquakeFragment.viewModel
            tvHeader.text = getString(R.string.near_earthquake)
            nowEarthquakeAdapter =
                NowEarthquakeAdapter(this@NearEarthquakeFragment.viewModel, false)

            swipeRefreshLayout.setOnRefreshListener {
                this@NearEarthquakeFragment.viewModel.apply {
                    getNearEarthquakeList.postValue(null)
                    isNearPage.postValue(true)
                    getLocationFromUser()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun observeEarthquakeData() {
        with(viewModel) {
            getNearLocationFilter()

            //Map icon Click
            clickableHeaderMenus.observe(viewLifecycleOwner) {
                if (it) {
                    clickMap.observe(viewLifecycleOwner) {
                        NavHandler.instance.toMapsActivity(requireContext(), filterNearList, true)
                    }
                }
            }

            error.observe(viewLifecycleOwner) { Err ->
                Err?.message?.let { showToast(it) }
            }
        }
    }

    private fun getLocationFromUser() {
        if (!isLocationEnabled(requireContext()))
            enableLocation(requireActivity(), requestOldLocationPermissionLauncher)
        else {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latLng = LatLng(location.latitude, location.longitude)
                    viewModel.earthquakeBodyRequest.userLat = location.latitude
                    viewModel.earthquakeBodyRequest.userLong = location.longitude
                    observeEarthquakeData()
                } else {
                    //Eski Konum Yoksa Şu anki konumu dinlemeliyiz
                    requestNowLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    currentLocation()
                    getLocationFromUser()
                }
            }
        }
    }

    private fun updateUiAfterSearch(text: String, isValid: Boolean) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            viewModel.earthquakeBodyRequest.location = text.lowercase()
            observeEarthquakeData()
        }
    }

    private fun currentLocation() {
        location?.initializeLocation()
        location =
            GetCurrentLocation(requireActivity() as MainActivity, object : CurrentLocationListener {
                override fun locationResponse(locationResult: LocationResult) {
                    latLng = locationResult.lastLocation?.latitude?.let { lat ->
                        locationResult.lastLocation?.longitude?.let { long ->
                            LatLng(lat, long)
                        }
                    }.apply {
                        location?.stopUpdateLocation()
                        viewModel.earthquakeBodyRequest.userLat = latLng?.latitude
                        viewModel.earthquakeBodyRequest.userLong = latLng?.longitude
                        observeEarthquakeData()
                    }
                }
            })
    }

    private val requestNowLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted)
                activity?.recreate()
            else
                grantedPermissionMainAction(requireContext())
        }

    private var requestOldLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                activity?.recreate()
            else
                grantedPermissionMainAction(requireContext())
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        location?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        location?.initializeLocation()
        getLocationFromUser()
    }

    override fun onPause() {
        super.onPause()
        location?.stopUpdateLocation()
        viewModel.cancelDataFetching()
    }
}