package com.ferdidrgn.anlikdepremler.presentation.ui.main

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.base.Err
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.useCase.GetEarthquakeUseCase
import kotlinx.coroutines.flow.collectLatest
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData
import com.ferdidrgn.anlikdepremler.domain.useCase.GetExampleHomeSliderUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTopTenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetTopTenLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.presentation.ui.main.home.SliderDetailsAdapterListener
import com.ferdidrgn.anlikdepremler.presentation.ui.main.home.TopTenEarthquakeAdapterListener
import com.ferdidrgn.anlikdepremler.presentation.ui.main.home.TopTenLocationEarthquakeAdapterListener
import com.ferdidrgn.anlikdepremler.tools.*
import com.ferdidrgn.anlikdepremler.tools.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.presentation.ui.mapsEarthquake.NowEarthQuakeAdapterListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getExampleHomeSliderUseCase: GetExampleHomeSliderUseCase,
    private val getEarthquakeUseCase: GetEarthquakeUseCase,
    private val getTopTenEarthquakeUseCase: GetTopTenEarthquakeUseCase,
    private val getTopTenLocationEarthquakeUseCase: GetTopTenLocationEarthquakeUseCase,
) : BaseViewModel(), NowEarthQuakeAdapterListener, SliderDetailsAdapterListener,
    TopTenLocationEarthquakeAdapterListener, TopTenEarthquakeAdapterListener {

    private var job: Job? = null

    //XML
    var location = MutableStateFlow("")

    //For Near Location Page
    var userLat = MutableStateFlow<Double?>(null)
    var userLong = MutableStateFlow<Double?>(null)
    var nearLocationList = ArrayList<Earthquake>()

    //Get Api List
    var getNowEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getNearEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getTopTenEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getTopTenLocationEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var homeSliderList = MutableLiveData<List<HomeSliderData>>()

    var isNearPage = MutableLiveData(false)
    var clickableHeaderMenus = MutableLiveData<Boolean>()

    //XML Click
    val clickList = LiveEvent<Boolean>()
    val clickClose = LiveEvent<Boolean>()
    val clickMap = LiveEvent<Boolean>()
    val clickFilter = LiveEvent<Boolean>()

    val clickSeeAllNowEarthquake = LiveEvent<Boolean>()
    val clickSeeAllLocationEarthquake = LiveEvent<Boolean>()

    init {
        falseToNearPageAndClickableMenus()
    }

    fun getHomePage() {
        ioScope {
            showLoading()

            getExampleHomeSliderUseCase().collectLatest { response ->
                response.let { homeSlider ->
                    homeSliderList.postValue(homeSlider)
                }
            }

            getTopTenEarthquake()

            if (location.value.isEmpty())
                location.emit("Istanbul")
            getTopTenLocationEarthquake()

            hideLoading()
        }
    }

    private fun getTopTenEarthquake() {
        mainScope {
            showLoading()
            getTopTenEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getTopTenEarthquake ->
                            getTopTenEarthquakeList.postValue(getTopTenEarthquake)
                            hideLoading()
                        }
                    }

                    is Resource.Error -> {
                        serverMessage(response.error)
                        hideLoading()
                    }

                    else -> hideLoading()
                }
            }
        }
    }

    fun getTopTenLocationEarthquake() {
        mainScope {
            showLoading()
            getTopTenLocationEarthquakeUseCase(location.value).collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getTopTenLocationEarthquake ->
                            getTopTenLocationEarthquakeList.postValue(getTopTenLocationEarthquake)
                            hideLoading()
                        }
                    }

                    is Resource.Error -> {
                        serverMessage(Err(message = message(R.string.error_empty_response)))
                        hideLoading()
                    }

                    else -> hideLoading()
                }
            }
        }
    }

    fun getNowEarthquake() {
        mainScope {
            showLoading()
            falseToNearPageAndClickableMenus()

            //collectlatest -> eger 2 defa emit edilirse ilk emit iptal olur 2. emit calisir. ilk emit bitmeden 2.ye gecilir
            //collect -> tum emitler calisir, ilk emitin bitmesini bekler sonra digerine gecer.
            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getEarthquake ->
                            getNowEarthquakeList.postValue(getEarthquake)
                            clickableHeaderMenus.postValue(true)
                        }
                        hideLoading()
                    }

                    is Resource.Error -> {
                        serverMessage(response.error)
                        hideLoading()
                    }

                    else -> hideLoading()
                }
            }
        }
    }

    fun getNearLocationFilter(isNearPage: Boolean) {
        job = mainScope {
            showLoading()
            clickableHeaderMenus.postValue(false)
            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        getNearEarthquakeList.postValue(null)
                        response.data?.let { getEarthquake ->
                            nearLocationList =
                                userLat.value?.let { lat ->
                                    userLong.value?.let { long ->
                                        getLocationFilterManuel(lat, long, getEarthquake)
                                    }
                                } ?: ArrayList()

                            this@MainViewModel.isNearPage.postValue(isNearPage)
                            getNearEarthquakeList.postValue(nearLocationList)
                            clickableHeaderMenus.postValue(true)
                        }
                        hideLoading()
                    }

                    is Resource.Error -> {
                        serverMessage(response.error)
                        hideLoading()
                    }

                    else -> hideLoading()
                }
            }
        }
    }

    private fun falseToNearPageAndClickableMenus() {
        isNearPage.postValue(false)
        clickableHeaderMenus.postValue(false)
    }

    fun cancelDataFetching() {
        if (job?.isActive == true)
            job?.cancel()
    }


    //Click Events
    fun onClickMap() {
        if (clickableHeaderMenus.value == true)
            clickMap.postValue(true)
    }

    fun onClickList() {
        clickList.postValue(true)
    }

    fun onClickClose() {
        clickClose.postValue(true)
    }

    fun onClickFilter() {
        if (clickableHeaderMenus.value == true)
            clickFilter.postValue(true)
    }

    fun onTopTenEarthquake() {
        clickSeeAllNowEarthquake.postValue(true)
    }

    fun onTopLocationEarthquake() {
        clickSeeAllLocationEarthquake.postValue(true)
    }

}