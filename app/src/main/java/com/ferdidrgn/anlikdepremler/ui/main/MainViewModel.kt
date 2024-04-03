package com.ferdidrgn.anlikdepremler.ui.main

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.filter.checkBetweenData
import com.ferdidrgn.anlikdepremler.filter.checkMapManuelStatus
import com.ferdidrgn.anlikdepremler.filter.getAllFilterQueriers
import com.ferdidrgn.anlikdepremler.filter.getFilterOneWeek
import com.ferdidrgn.anlikdepremler.filter.getLocationFilterManuel
import com.ferdidrgn.anlikdepremler.model.Earthquake
import com.ferdidrgn.anlikdepremler.model.HomeSliderData
import com.ferdidrgn.anlikdepremler.model.dummyModel.EarthquakeBodyRequest
import com.ferdidrgn.anlikdepremler.repository.EarthquakeRepository
import com.ferdidrgn.anlikdepremler.repository.HomeSliderRepository
import com.ferdidrgn.anlikdepremler.tools.*
import com.ferdidrgn.anlikdepremler.tools.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.ui.main.home.SliderDetailsAdapterListener
import com.ferdidrgn.anlikdepremler.ui.main.home.TopTenEarthquakeAdapterListener
import com.ferdidrgn.anlikdepremler.ui.main.home.TopTenLocationEarthquakeAdapterListener
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
    TopTenLocationEarthquakeAdapterListener, TopTenEarthquakeAdapterListener {

    private var job: Job? = null

    //XML ve filter
    var earthquakeBodyRequest = EarthquakeBodyRequest()
    var location = MutableStateFlow("")
    var ml = MutableStateFlow("")
    var startDate = MutableStateFlow("")
    var endDate = MutableStateFlow("")
    var onlyDate = MutableStateFlow("")

    var filterNowList = ArrayList<Earthquake>()
    var filterNearList = ArrayList<Earthquake>()

    //Get Api List
    var getNowEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getNearEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getDateEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getLocationApiEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getTopTenEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getTopTenLocationEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var homeSliderList = MutableLiveData<List<HomeSliderData>>()

    var isNearPage = MutableLiveData(false)
    var clickableHeaderMenus = MutableLiveData<Boolean>()

    //XML Click
    val clickMap = LiveEvent<Boolean>()
    val clickList = LiveEvent<Boolean>()
    val clickClose = LiveEvent<Boolean>()
    val clickApply = LiveEvent<Boolean>()
    val clickFilterClear = LiveEvent<Boolean>()

    val clickSeeAllNowEarthquake = LiveEvent<Boolean>()
    val clickSeeAllLocationEarthquake = LiveEvent<Boolean>()

    val selectedOption = MutableLiveData<Int>()

    val subOption = MutableLiveData(false)

    init {
        selectedOption.value = -1
        falseToNearPageAndClickableMenus()
    }

    fun getHomePage() {
        mainScope {
            showLoading()
            homeSliderList.value = homeSliderRepository.createExampleHomeSliderList()

            //Top Ten All Earthquake
            getTopTenEarthquake()

            //Top Ten Location Earthquake
            location.emit("Istanbul")
            getTopTenLocationEarthquake()
        }
    }

    private fun getTopTenEarthquake() {
        mainScope {
            showLoading()
            when (val response = earthquakeRepository.getTopTenEarthquakeList()) {
                is Resource.Success -> {
                    response.data?.let { getTopTenEarthquake ->
                        getTopTenEarthquakeList.postValue(getTopTenEarthquake)
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

    fun getTopTenLocationEarthquake() {
        mainScope {
            showLoading()
            when (val response =
                earthquakeRepository.getTopTenLocationEarthquakeList(location.value)) {
                is Resource.Success -> {
                    response.data?.let { getTopTenLocationEarthquake ->
                        getTopTenLocationEarthquakeList.postValue(getTopTenLocationEarthquake)
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
            falseToNearPageAndClickableMenus()

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
        earthquakeBodyRequest.apply {
            location = this@MainViewModel.location.value
            ml = this@MainViewModel.ml.value
            onlyDate = this@MainViewModel.onlyDate.value
            startDate = this@MainViewModel.startDate.value
            endDate = this@MainViewModel.endDate.value

            if (ml.isEmpty() && !checkMapManuelStatus(userLat ?: 0.0, userLong ?: 0.0) &&
                !checkBetweenData(startDate, endDate)
            ) {
                filterList = getNowEarthquakeList.value.let { it!! }
                timeHideLoading()
            } else {
                getAllFilterQueriers(
                    this,
                    getNowEarthquakeList.value.let { it!! },
                ) { returnedFilterList ->
                    filterList = returnedFilterList
                }
                timeHideLoading()
            }
        }
        return filterList
    }

    fun getNearLocationFilter() {
        job = mainScope {
            showLoading()
            clickableHeaderMenus.postValue(false)
            when (val response = earthquakeRepository.getEarthquake()) {
                is Resource.Success -> {
                    filterNearList.clear()
                    showToast("lat" + earthquakeBodyRequest.userLat + "long" + earthquakeBodyRequest.userLong)
                    response.data?.let { getEarthquake ->
                        earthquakeBodyRequest.userLat?.let { lat ->
                            earthquakeBodyRequest.userLong?.let { long ->
                                filterNearList = getLocationFilterManuel(lat, long, getEarthquake)
                            }
                        } ?: run {
                            filterNearList =
                                getLocationFilterManuel(LAT_LAT, LAT_LONG, getEarthquake)
                        }
                    }
                    showToast("/////filerList" + getNearEarthquakeList.value)
                    getNearEarthquakeList.postValue(filterNearList)
                    showToast("/////getNearList" + getNearEarthquakeList.value)
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

    fun getLocationApi() {
        mainScope {
            showLoading()
            when (val response = earthquakeRepository.getLocationEarthquakeList(location.value)) {
                is Resource.Success -> {
                    response.data?.let { getLocationEarthquake ->
                        getLocationApiEarthquakeList.postValue(getLocationEarthquake)
                        getNowEarthquakeList.postValue(getLocationEarthquake)
                    }
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

    fun getDataBetweenApi() {
        mainScope {
            showLoading()
            showToast("Başlangıç: ${startDate.value} Bitiş: ${endDate.value}")
            when (val response =
                earthquakeRepository.getDateBetweenEarthquakeList(startDate.value, endDate.value)) {
                is Resource.Success -> {
                    response.data?.let { getDataEarthquake ->
                        getDateEarthquakeList.postValue(getDataEarthquake)
                        getNowEarthquakeList.postValue(getDataEarthquake)
                    }
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

    fun getOnlyDataApi() {
        mainScope {
            showLoading()
            when (val response = earthquakeRepository.getOnlyDateEarthquakeList(onlyDate.value)) {
                is Resource.Success -> {
                    response.data?.let { getDataEarthquake ->
                        getDateEarthquakeList.postValue(getDataEarthquake)
                        getNowEarthquakeList.postValue(getDataEarthquake)
                    }
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

    private fun falseToNearPageAndClickableMenus() {
        isNearPage.postValue(false)
        clickableHeaderMenus.postValue(false)
    }

    fun clearXmlData() {
        earthquakeBodyRequest = EarthquakeBodyRequest()
        location.value = ""
        ml.value = ""
        onlyDate.value = ""
        startDate.value = ""
        endDate.value = ""
    }

    fun cancelDataFetching() {
        if (job?.isActive == true) {
            job?.cancel()
        }
    }

    //Listeners
    override fun onSliderDetailsAdapterListener(homeSliderData: HomeSliderData) {}
    override fun onNowEarthquakeItemClicked(position: Int) {}
    override fun onTopTenEarthquakeAdapterListener(earthquake: Earthquake) {}
    override fun onTopTenLocationEarthquakeAdapterListener(earthquake: Earthquake) {}


    //Click Events
    fun onClickMap() {
        if (clickableHeaderMenus.value == true)
            clickMap.postValue(true)
    }

    fun onClickList() {
        clickList.postValue(true)
    }

    fun onClickClear() {
        clickFilterClear.postValue(true)
    }

    fun onClickClose() {
        clickClose.postValue(true)
    }

    fun onClickApply() {
        clickApply.postValue(true)

        if (selectedOption.value == 1) {
            if (subOption.value == true)
                getNearLocationFilter()
            else
                getLocationApi()
        } else if (selectedOption.value == 2) {
            if (subOption.value == true)
                getOnlyDataApi()
            else
                getDataBetweenApi()
        } else if (selectedOption.value == 3) {
            earthquakeBodyRequest.apply {
                userLat = null
                userLong = null
            }
            startDate.value = ""
            endDate.value = ""
            getFilters()
        }
    }

    fun onOptionSelected(option: Int) {
        selectedOption.value = option
    }

    fun onSubOptionChangeStatus() {
        subOption.postValue(subOption.value?.not())
    }

    fun onTopTenEarthquake() {
        clickSeeAllNowEarthquake.postValue(true)
    }

    fun onTopLocationEarthquake() {
        clickSeeAllLocationEarthquake.postValue(true)
    }

}