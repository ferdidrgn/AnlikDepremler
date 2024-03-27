package com.ferdidrgn.anlikdepremler.ui.main.nearEarthquake

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentNearEarthquakeBinding
import com.ferdidrgn.anlikdepremler.enums.ToMain
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
    private var latLng: LatLng? = null
    var location: GetCurrentLocation? = null

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentNearEarthquakeBinding =
        FragmentNearEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        builderADS(requireContext(), binding.adView)

        binding.includeEarthquakeList.viewModel = viewModel
        binding.includeEarthquakeList.tvHeader.text = getString(R.string.near_earthquake)
        binding.includeEarthquakeList.llFilters.hide()
        //binding.includeEarthquakeList.tvFilter.text = getString(R.string.this_week_filter)
        binding.includeEarthquakeList.nowEarthquakeAdapter = NowEarthquakeAdapter(viewModel, false)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        observeEarthquakeData()

    }

    private fun observeEarthquakeData() {
        with(viewModel) {
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
        if (!isLocationEnabled(requireContext())) {
            enableLocation(requireActivity(), launcher)
        }
        currentLocation()
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
        location =
            GetCurrentLocation(requireActivity() as MainActivity, object : locationListener {
                override fun locationResponse(locationResult: LocationResult) {
                    locationResult.lastLocation?.latitude?.let { lat ->
                        locationResult.lastLocation?.longitude?.let { long ->
                            latLng = LatLng(lat, long)
                            ClientPreferences.inst.userLat = lat.toFloat()
                            ClientPreferences.inst.userLong = long.toFloat()
                        }
                    } ?: kotlin.run {
                        LatLng(LAT_LAT, LAT_LONG)
                    }

                    viewModel.apply {
                        this@NearEarthquakeFragment.location?.stopUpdateLocation()
                        earthquakeBodyRequest.userLat = latLng?.latitude
                        earthquakeBodyRequest.userLong = latLng?.longitude
                        getLocationFilter()
                    }
                }
            })
    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentLocation()
            } else {
                NavHandler.instance.toMainActivityClearTask(
                    requireContext(),
                    ToMain.Home
                )
                showToast(getString(R.string.please_accept_location))
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        location?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        location?.inicializeLocation()
    }

    override fun onResume() {
        super.onResume()
        getLocationFromUser()
    }

    override fun onPause() {
        super.onPause()
        location?.stopUpdateLocation()
        viewModel.isNearPage.postValue(false)
        viewModel.cancelDataFetching()
    }

}