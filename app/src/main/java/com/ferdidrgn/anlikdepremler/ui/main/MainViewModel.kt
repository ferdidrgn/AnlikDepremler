package com.ferdidrgn.anlikdepremler.ui.main

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.filter.getAllFilterQueriers
import com.ferdidrgn.anlikdepremler.filter.getFilterOneWeek
import com.ferdidrgn.anlikdepremler.filter.getLocationFilter
import com.ferdidrgn.anlikdepremler.model.Earthquake
import com.ferdidrgn.anlikdepremler.model.HomeSliderData
import com.ferdidrgn.anlikdepremler.model.dummyModel.EarthquakeBodyRequest
import com.ferdidrgn.anlikdepremler.repository.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.repository.HomeSliderRepository
import com.ferdidrgn.anlikdepremler.tools.*
import com.ferdidrgn.anlikdepremler.tools.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.ui.main.home.SliderDetailsAdapterListener
import com.ferdidrgn.anlikdepremler.ui.main.home.TopTenEarthquakeAdapterListener
import com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake.NowEarthQuakeAdapterListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel @Inject constructor(
    private val earthquakeRepository: EarthquakeRepository,
    private val homeSliderRepository: HomeSliderRepository,
) : BaseViewModel(), NowEarthQuakeAdapterListener, SliderDetailsAdapterListener,
    TopTenEarthquakeAdapterListener {

    var earthquakeBodyRequest = EarthquakeBodyRequest()
    private var job: Job? = null

    var getNowEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getNearEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var filterNowList = ArrayList<Earthquake>()
    var filterNearList = ArrayList<Earthquake>()

    var getTopTenEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var homeSliderList = MutableLiveData<List<HomeSliderData>>()

    var ml = MutableStateFlow("")
    var search = MutableStateFlow("")
    var startDate = MutableStateFlow("")
    var endDate = MutableStateFlow("")

    var isNearPage = MutableLiveData(false)
    var clickableHeaderMenus = LiveEvent<Boolean>()

    val clickMap = LiveEvent<Boolean>()
    val clickList = LiveEvent<Boolean>()
    val clickClear = LiveEvent<Boolean>()
    val clickClose = LiveEvent<Boolean>()
    val clickApply = LiveEvent<Boolean>()

    val clickSeeAllNowEarthquake = LiveEvent<Boolean>()

    init {
        changeStatus(false)
    }

    fun getHomePage() {
        mainScope {
            showLoading()
            homeSliderList.value = homeSliderRepository.createExampleHomeSliderList()

            //Top Five Earthquake
            when (val response = earthquakeRepository.getTopTenEarthquakeList()) {
                is Resource.Success -> {
                    response.data?.let { getTopFiveEarthquake ->
                        getTopTenEarthquakeList.postValue(getTopFiveEarthquake)
                        timeHideLoading()
                    }
                }

                is Resource.Error -> {
                    serverMessage(response.error)
                    hideLoading()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    fun getNowEarthquake() {
        mainScope {
            showLoading()

            changeStatus(false)
            when (val response = earthquakeRepository.getEarthquake()) {
                is Resource.Success -> {
                    response.data?.let { getEarthquake ->
                        getNowEarthquakeList.postValue(getEarthquake)
                        clickableHeaderMenus.postValue(true)
                        timeHideLoading()
                    }
                }

                is Resource.Error -> {
                    serverMessage(response.error)
                    hideLoading()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    fun getFilters() {
        mainScope {
            showLoading()

            when (val response = earthquakeRepository.getEarthquake()) {
                is Resource.Success -> {
                    response.data?.let { getEarthquake ->
                        getNowEarthquakeList.postValue(getEarthquake)
                    }
                    filterNowList = getAllFilter() ?: ArrayList()
                    getNowEarthquakeList.postValue(filterNowList)
                    timeHideLoading()
                }

                is Resource.Error -> {
                    serverMessage(response.error)
                    hideLoading()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    private fun getAllFilter(): ArrayList<Earthquake>? {
        showLoading()
        var filterList: ArrayList<Earthquake>? = null
        earthquakeBodyRequest.ml = ml.value
        earthquakeBodyRequest.startDate = startDate.value
        earthquakeBodyRequest.endDate = endDate.value

        getAllFilterQueriers(
            earthquakeBodyRequest,
            getNowEarthquakeList.value.let { it!! },
        ) { returnedFilterList ->
            filterList = (returnedFilterList)
        }
        timeHideLoading()
        return filterList
    }

    fun getLocationFilter() {
        job = mainScope {
            showLoading()
            clickableHeaderMenus.postValue(false)
            when (val response = earthquakeRepository.getEarthquake()) {
                is Resource.Success -> {
                    filterNearList.clear()
                    response.data?.let { getEarthquake ->
                        earthquakeBodyRequest.userLat?.let { lat ->
                            earthquakeBodyRequest.userLong?.let { long ->
                                filterNearList = getLocationFilter(lat, long, getEarthquake)
                            }
                        }
                    }
                    changeStatus(true)
                    getNearEarthquakeList.postValue(filterNearList)
                    timeHideLoading()
                }

                is Resource.Error -> {
                    serverMessage(response.error)
                    hideLoading()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    fun cancelDataFetching() {
        // Eğer işlem çalışıyorsa, iptal et
        if (job?.isActive == true) {
            job?.cancel()
        }
    }

    fun getThisWeekFilter() {
        mainScope {
            showLoading()

            when (val response = earthquakeRepository.getEarthquake()) {
                is Resource.Success -> {
                    response.data?.let { getEarthquake ->
                        getNowEarthquakeList.postValue(getEarthquake)
                    }
                    clickableHeaderMenus.postValue(true)
                    getNowEarthquakeList.value?.forEach { earthquake ->
                        filterNowList = getFilterOneWeek(earthquake)
                    }
                    getNowEarthquakeList.postValue(filterNowList)

                    timeHideLoading()
                }

                is Resource.Error -> {
                    serverMessage(response.error)
                    hideLoading()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    private fun changeStatus(value: Boolean) {
        isNearPage.postValue(value)
        clickableHeaderMenus.postValue(value)
    }

    override fun onSliderDetailsAdapterListener(homeSliderData: HomeSliderData) {}
    override fun onNowEarthquakeItemClicked(position: Int) {}
    override fun onTopTenEarthquakeAdapterListener(earthquake: Earthquake) {}


    //Click Events
    fun onClickMap() {
        if (clickableHeaderMenus.value == true)
            clickMap.postValue(true)
    }

    fun onClickList() {
        clickList.postValue(true)
    }

    fun onClickClear() {
        clickClear.postValue(true)
    }

    fun onClickClose() {
        clickClose.postValue(true)
    }

    fun onClickApply() {
        clickApply.postValue(true)
    }

    fun onNowEartquake() {
        clickSeeAllNowEarthquake.postValue(true)
    }

}