package com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.FragmentMapsNowEarthquakeBinding
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.presentation.main.MainViewModel
import com.ferdidrgn.anlikdepremler.util.handler.NavHandler
import com.ferdidrgn.anlikdepremler.util.helpers.ClientPreferences
import com.ferdidrgn.anlikdepremler.util.helpers.CurrentLocationListener
import com.ferdidrgn.anlikdepremler.util.helpers.LAT_LAT
import com.ferdidrgn.anlikdepremler.util.helpers.LAT_LONG
import com.ferdidrgn.anlikdepremler.util.helpers.LocationManager
import com.ferdidrgn.anlikdepremler.util.helpers.NEAR_EARTHQUAKE
import com.ferdidrgn.anlikdepremler.util.helpers.ToMain
import com.ferdidrgn.anlikdepremler.util.helpers.enableLocation
import com.ferdidrgn.anlikdepremler.util.helpers.isLocationEnabled
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope
import com.ferdidrgn.anlikdepremler.util.helpers.showToast
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MapsEarthquakeActivity : BaseActivity<MainViewModel, FragmentMapsNowEarthquakeBinding>() {

    lateinit var gMap: GoogleMap
    var location: LocationManager? = null
    private var latLng: LatLng? = null
    private var earthquakeList = ArrayList<Earthquake>()
    private var isNearEarthquake: Boolean? = null
    private var job: Job? = null

    override fun getVM(): Lazy<MainViewModel> = viewModels()

    override fun getDataBinding(): FragmentMapsNowEarthquakeBinding =
        FragmentMapsNowEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {
        binding.viewModel = viewModel
        setAds(binding.adView)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.nowEarthquakeAdapter = NowEarthquakeAdapter(viewModel, true)

        observeArgumentsData()
        observeEarthquakeData()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9)
            setUpMap()
    }

    private fun observeArgumentsData() {
        isNearEarthquake = intent.getBooleanExtra(NEAR_EARTHQUAKE, false)

        if (isNearEarthquake == true)
            getLocation()
        else
            viewModel.getNowEarthquake()
    }

    private fun observeEarthquakeData() {
        with(viewModel) {

            //Click Events
            clickClose.observe(this@MapsEarthquakeActivity) {
                whereActionPage()
            }

            clickList.observe(this@MapsEarthquakeActivity) {
                whereActionPage()
            }

            //Observe
            getNearEarthquakeList.observe(this@MapsEarthquakeActivity) { dataResponse ->
                if (isNearEarthquake == true)
                    mapEarthquakeList.postValue(dataResponse)
            }

            getNowEarthquakeList.observe(this@MapsEarthquakeActivity) { dataResponse ->
                if (isNearEarthquake == false)
                    mapEarthquakeList.postValue(dataResponse)
            }

            mapEarthquakeList.observe(this@MapsEarthquakeActivity) { dataResponse ->
                earthquakeList = dataResponse ?: arrayListOf()
                setUpEarthquakeAdapter()
            }

            error.observe(this@MapsEarthquakeActivity) { errorApi ->
                showToast(errorApi?.message.toString())
            }
        }
    }

    private var requestOldLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) getLocation()
            else grantedPermissionMainAction()
        }

    private val requestNowLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setUpMap()
            } else
                grantedPermissionMainAction()
        }

    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        gMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this@MapsEarthquakeActivity, R.raw.map_style)
        )
        gMap.setOnMarkerClickListener { marker ->
            val item = earthquakeList.find { earthquake ->
                marker.position.latitude == earthquake.latitude?.toDouble() &&
                        marker.position.longitude == earthquake.longitude?.toDouble()
            } ?: return@setOnMarkerClickListener false

            val position = earthquakeList.indexOf(item)
            binding.rvMap.apply {
                scrollToPosition(position)
                /*smoothScroller = CenterSmoothScroller(requireContext())
                val snapHelper: SnapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(rvMap)
                onFlingListener = null
                adapter = this@MapsNowEarthquake.adapter
                itemAnimator = null*/
            }
            true
        }

        if (isNearEarthquake == true)
            requestNowLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        gMap.isMyLocationEnabled = false
        if (isNearEarthquake == true)
            getLocation()
    }

    private fun getLocation() {
        if (!isLocationEnabled(this))
            enableLocation(this, requestOldLocationPermissionLauncher)
        else {
            //Permission control is done in LocationManager.
            location = LocationManager(this, object : CurrentLocationListener {
                override fun locationResponse(locationResult: LocationResult) {}
            })

            location?.getLastKnownLocation { location ->
                if (location != null) {
                    mainScope {
                        latLng = LatLng(location.latitude, location.longitude)
                        viewModel.userLat.emit(location.latitude)
                        viewModel.userLong.emit(location.longitude)
                        viewModel.getNearLocationFilter()
                        observeEarthquakeData()
                    }
                } else {
                    //Eski Konum Yoksa Şu anki konumu dinlemeliyiz
                    requestNowLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                latLng?.let {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 17f)
                    gMap.animateCamera(cameraUpdate)
                }
            }
        }
    }

    private fun setUpEarthquakeAdapter() {
        val newAdapter = NowEarthquakeAdapter(viewModel, true)

        binding.rvMap.apply {
            val linearLayoutManager = LinearLayoutManager(
                this@MapsEarthquakeActivity, LinearLayoutManager.HORIZONTAL, false
            )
            //var smoothScroller: RecyclerView.SmoothScroller = CenterSmoothScroller(this@MapsEarthquakeActivity)
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this@apply)
            onFlingListener = null
            itemAnimator = null

            //this@MapsEarthquakeActivity.adapter.updateData(earthquakeList)
            adapter = newAdapter
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    var position = 0
                    position = if (dx < 0) {
                        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() > -1)
                            linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                        else
                            linearLayoutManager.findFirstVisibleItemPosition()

                    } else {
                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() > -1)
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        else
                            linearLayoutManager.findLastVisibleItemPosition()
                    }

                    if (earthquakeList.size - 1 >= position && position > -1) {
                        earthquakeList[position].apply {
                            if (latitude != null && longitude != null) {
                                latLng = LatLng(latitude.toDouble(), longitude.toDouble())
                                latLng?.let {
                                    val latLng1 = latLng
                                    val cameraUpdate =
                                        CameraUpdateFactory.newLatLngZoom(
                                            latLng1
                                                ?: throw NullPointerException("Expression 'latLng' must not be null"),
                                            8f
                                        )
                                    gMap.animateCamera(cameraUpdate)
                                }
                            } else {
                                ClientPreferences.inst.apply {
                                    latLng = if (userLat != null && userLong != null)
                                        LatLng(userLat!!.toDouble(), userLong!!.toDouble())
                                    else LatLng(LAT_LAT, LAT_LONG)
                                }
                                latLng?.let {
                                    val latLng1 = latLng
                                    val cameraUpdate =
                                        CameraUpdateFactory.newLatLngZoom(
                                            latLng1
                                                ?: throw NullPointerException("Expression 'latLng' must not be null"),
                                            8f
                                        )
                                    gMap.animateCamera(cameraUpdate)
                                }
                            }
                        }
                    }
                }
            })
        }
        newAdapter.addMarker { earthquake ->
            addMarker(earthquake)
        }
    }

    private fun addMarker(earthquake: Earthquake) {
        //İlk önce lokasyon var mı yok mu kontrol ediyoruz. İçeride de ml değeri var mı yok mu kontrol ediyoruz.
        if (earthquake.latitude != null && earthquake.longitude != null) {
            if (earthquake.ml.isNullOrEmpty().not()) {
                CoroutineScope(Dispatchers.Main).launch {

                    val bm = addMarkerMl(earthquake.ml)

                    bm.let { bitmap ->
                        earthquake.latitude.let { lat ->
                            earthquake.longitude.let { long ->
                                LatLng(
                                    lat.toDouble(), long.toDouble()
                                )
                            }
                        }
                            ?.let { location ->
                                setMarkers(
                                    bitmap, earthquake.ml.toString(), location
                                )
                            }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {

                    val bm = addMarkerMl("0.0")

                    bm.let { bitmap ->
                        earthquake.latitude.let { lat ->
                            earthquake.longitude.let { long ->
                                LatLng(
                                    lat.toDouble(), long.toDouble()
                                )
                            }
                        }
                            ?.let { location ->
                                setMarkers(
                                    bitmap, "0.0", location
                                )
                            }
                    }
                }
            }
        } else {
            if (earthquake.ml.isNullOrEmpty().not()) {
                CoroutineScope(Dispatchers.Main).launch {

                    val bm = addMarkerMl(earthquake.ml)

                    bm.let { bitmap ->
                        LAT_LAT.let { lat ->
                            LAT_LONG.let { long ->
                                LatLng(
                                    lat, long
                                )
                            }
                        }
                            .let { location ->
                                setMarkers(
                                    bitmap, earthquake.ml.toString(), location
                                )
                            }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {

                    val bm = addMarkerMl("0.0")

                    bm.let { bitmap ->
                        LAT_LAT.let { lat ->
                            LAT_LONG.let { long ->
                                LatLng(
                                    lat.toDouble(), long.toDouble()
                                )
                            }
                        }
                            .let { location ->
                                setMarkers(
                                    bitmap, "0.0", location
                                )
                            }
                    }
                }
            }
        }
    }

    private fun addMarkerMl(ml: String?): Bitmap {
        val customMarkerView = binding.mapMarker
        binding.mapMarker.tvMl.text = ml

        customMarkerView.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.root.layout(
            0,
            0,
            customMarkerView.root.measuredWidth,
            customMarkerView.root.measuredHeight
        )
        customMarkerView.root.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.root.measuredWidth, customMarkerView.root.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.root.background
        drawable?.draw(canvas)
        customMarkerView.root.draw(canvas)
        return returnedBitmap
    }

    private fun setMarkers(image: Bitmap?, title: String, latLng: LatLng) {
        gMap.addMarker(
            MarkerOptions()
                .icon(image?.let { BitmapDescriptorFactory.fromBitmap(image) })
                .title(title)
                .position(latLng)
        )
    }

    private fun whereActionPage() {
        when (isNearEarthquake) {
            true -> NavHandler.instance.toMainActivity(this, ToMain.NearEarthquake)
            else -> NavHandler.instance.toMainActivity(this, ToMain.NowEarthquake)
        }
    }

    private fun grantedPermissionMainAction() {
        NavHandler.instance.toMainActivity(this, ToMain.Home, onlyClearTask = true)
        showToast(getString(R.string.please_accept_location))
    }

    override fun onResume() {
        super.onResume()
        setAds(binding.adView)
    }

    override fun onDestroy() {
        super.onDestroy()

        location?.stopUpdateLocation()
        viewModel.getNowEarthquakeList.postValue(null)
        binding.unbind()
        networkMonitor.unregister()
    }
}