package com.ferdidrgn.anlikdepremler.presentation.main.nearEarthquake

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentNearEarthquakeBinding
import com.ferdidrgn.anlikdepremler.presentation.main.MainActivity
import com.ferdidrgn.anlikdepremler.util.handler.NavHandler
import com.ferdidrgn.anlikdepremler.presentation.main.MainViewModel
import com.ferdidrgn.anlikdepremler.util.helpers.ToMain
import com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake.NowEarthquakeAdapter
import com.ferdidrgn.anlikdepremler.util.helpers.CurrentLocationListener
import com.ferdidrgn.anlikdepremler.util.helpers.LocationManager
import com.ferdidrgn.anlikdepremler.util.helpers.enableLocation
import com.ferdidrgn.anlikdepremler.util.helpers.isLocationEnabled
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope
import com.ferdidrgn.anlikdepremler.util.helpers.showToast
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class NearEarthquakeFragment :
    BaseFragment<MainViewModel, FragmentNearEarthquakeBinding>() {

    private var job: Job? = null
    var location: LocationManager? = null
    private var latLng: LatLng? = null

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentNearEarthquakeBinding =
        FragmentNearEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        setAds(binding.adView)

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

    }

    private fun observeEarthquakeData() {
        with(viewModel) {

            getNearLocationFilter()

            //Map icon Click
            clickableHeaderMenus.observe(viewLifecycleOwner) {
                if (it) {
                    clickMap.observe(viewLifecycleOwner) {
                        NavHandler.instance.toMapsActivity(requireContext(), true)
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
            //Permission control is done in LocationManager.
            location = LocationManager(requireContext(), object : CurrentLocationListener {
                override fun locationResponse(locationResult: LocationResult) {}
            })
            location?.getLastKnownLocation { location ->
                if (location != null) {
                    mainScope {
                        latLng = LatLng(location.latitude, location.longitude)
                        viewModel.userLat.emit(location.latitude)
                        viewModel.userLong.emit(location.longitude)
                        observeEarthquakeData()
                    }
                } else {
                    //Eski Konum Yoksa Şu anki konumu dinlemeliyiz
                    requestNowLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    private fun updateUiAfterSearch(text: String, isValid: Boolean) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            viewModel.location.emit(text.lowercase())
            observeEarthquakeData()
        }
    }

    private val requestNowLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) locationListener()
            else (requireActivity() as MainActivity).whereToGetBottomNavItem(ToMain.Home)
        }

    private var requestOldLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                getLocationFromUser()
            else
                (requireActivity() as MainActivity).whereToGetBottomNavItem(ToMain.Home)
        }

    private fun locationListener() {
        location = LocationManager(requireContext(), object : CurrentLocationListener {
            override fun locationResponse(locationResult: LocationResult) {
                latLng = locationResult.lastLocation?.latitude?.let { lat ->
                    locationResult.lastLocation?.longitude?.let { long ->
                        LatLng(lat, long)
                    }
                }
                mainScope {
                    viewModel.userLat.emit(latLng?.latitude)
                    viewModel.userLong.emit(latLng?.longitude)
                    location?.stopUpdateLocation()
                    getLocationFromUser()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setAds(binding.adView)
        getLocationFromUser()
    }

    override fun onPause() {
        super.onPause()
        location?.stopUpdateLocation()
        viewModel.getNearEarthquakeList.postValue(null)
        viewModel.cancelDataFetching()
    }
}