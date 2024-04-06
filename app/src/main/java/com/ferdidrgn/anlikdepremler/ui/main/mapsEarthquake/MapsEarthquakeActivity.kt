package com.ferdidrgn.anlikdepremler.ui.main.mapsEarthquake

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
import com.ferdidrgn.anlikdepremler.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.FragmentMapsNowEarthquakeBinding
import com.ferdidrgn.anlikdepremler.tools.enums.ToMain
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.tools.*
import com.ferdidrgn.anlikdepremler.tools.NavHandler
import com.ferdidrgn.anlikdepremler.ui.main.MainViewModel
import com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake.NowEarthquakeAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MapsEarthquakeActivity : BaseActivity<MainViewModel, FragmentMapsNowEarthquakeBinding>() {

    lateinit var gMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var location: LocationManager? = null
    private var latLng: LatLng? = null
    private var earthquakeList = ArrayList<Earthquake>()
    private var cameEarthquakeList = ArrayList<Earthquake>()
    private var isNearEarthquake = false

    override fun getVM(): Lazy<MainViewModel> = viewModels()

    override fun getDataBinding(): FragmentMapsNowEarthquakeBinding =
        FragmentMapsNowEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        builderADS(this, binding.adView)

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
        cameEarthquakeList = intent.getSerializableExtra(FILTER_LIST) as ArrayList<Earthquake>
        if (isNearEarthquake)
            getLocation()

        if (cameEarthquakeList.size > 0) {
            viewModel.getNowEarthquakeList.postValue(cameEarthquakeList)
        } else {
            if (!isNearEarthquake)
                viewModel.getNowEarthquake()
        }
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
                if (isNearPage.value == true) {
                    earthquakeList.clear()
                    dataResponse?.forEach { earthquake ->
                        earthquakeList.addAll(listOf(earthquake))
                    }
                    setUpEarthquakeAdapter()
                }
            }

            getNowEarthquakeList.observe(this@MapsEarthquakeActivity) { dataResponse ->
                if (isNearPage.value == false) {
                    dataResponse?.forEach { earthquake ->
                        earthquakeList.addAll(listOf(earthquake))
                    }
                    setUpEarthquakeAdapter()
                }
            }

            error.observe(this@MapsEarthquakeActivity) { errorApi ->
                showToast(errorApi?.message.toString())
            }
        }
    }

    private var requestOldLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                getLocation()
            else
                grantedPermissionMainAction(this)
        }

    private val requestNowLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted)
                setUpMap()
            else
                grantedPermissionMainAction(this)
        }

    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        gMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this@MapsEarthquakeActivity,
                R.raw.map_style
            )
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

        if (isNearEarthquake)
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
        if (isNearEarthquake)
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
                    latLng = LatLng(location.latitude, location.longitude)
                    viewModel.earthquakeBodyRequest.userLat = location.latitude
                    viewModel.earthquakeBodyRequest.userLong = location.longitude
                    viewModel.getNearLocationFilter(true)
                    observeEarthquakeData()
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
                        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() > -1) {
                            linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                        } else {
                            linearLayoutManager.findFirstVisibleItemPosition()
                        }
                    } else {
                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() > -1) {
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        } else {
                            linearLayoutManager.findLastVisibleItemPosition()
                        }
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
        if (isNearEarthquake)
            NavHandler.instance.toMainActivity(this, ToMain.NearEarthquake)
        else
            NavHandler.instance.toMainActivity(this, ToMain.NowEarthquake)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
        location?.stopUpdateLocation()
        viewModel.getNowEarthquakeList.postValue(null)
    }
}