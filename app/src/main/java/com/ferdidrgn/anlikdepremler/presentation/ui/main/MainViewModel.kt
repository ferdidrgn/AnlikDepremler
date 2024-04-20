package com.ferdidrgn.anlikdepremler.presentation.ui.main

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.base.Err
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.useCase.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetEarthquakeUseCase
import kotlinx.coroutines.flow.collectLatest
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.model.HomeSliderData
import com.ferdidrgn.anlikdepremler.domain.model.dummyModel.EarthquakeBodyRequest
import com.ferdidrgn.anlikdepremler.domain.useCase.GetExampleHomeSliderUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetOnlyDateEarthquakeUseCase
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
    private val getLocationEarthquakeUseCase: GetLocationEarthquakeUseCase,
    private val getTopTenEarthquakeUseCase: GetTopTenEarthquakeUseCase,
    private val getTopTenLocationEarthquakeUseCase: GetTopTenLocationEarthquakeUseCase,
    private val getOnlyDateEarthquakeUseCase: GetOnlyDateEarthquakeUseCase,
    private val getDateBetweenEarthquakeUseCase: GetDateBetweenEarthquakeUseCase,
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
    val clickList = LiveEvent<Boolean>()
    val clickClose = LiveEvent<Boolean>()
    val clickApply = LiveEvent<Boolean>()
    val clickMap = LiveEvent<Boolean>()
    val clickFilter = LiveEvent<Boolean>()
    val clickFilterClear = LiveEvent<Boolean>()

    val clickCstmDatePickerStartDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerEndDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerOnlyDate = LiveEvent<Boolean?>()

    val clickSeeAllNowEarthquake = LiveEvent<Boolean>()
    val clickSeeAllLocationEarthquake = LiveEvent<Boolean>()

    val selectedOption = MutableLiveData<Int>()

    val subOption = MutableLiveData(false)

    init {
        selectedOption.value = -1
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
                            getTopTenLocationEarthquakeList.postValue(
                                getTopTenLocationEarthquake
                            )
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

    fun getFilters() {
        mainScope {
            showLoading()

            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getEarthquake ->
                            getNowEarthquakeList.postValue(getEarthquake)
                        }
                        filterNowList = getAllFilter() ?: ArrayList()
                        getNowEarthquakeList.postValue(filterNowList)
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
                hideLoading()
            } else {
                getAllFilterQueriers(
                    this,
                    getNowEarthquakeList.value.let { it!! },
                ) { returnedFilterList ->
                    filterList = returnedFilterList
                }
                hideLoading()
            }
        }
        return filterList
    }

    fun getNearLocationFilter(isNearPage: Boolean) {
        job = mainScope {
            showLoading()
            clickableHeaderMenus.postValue(false)
            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        filterNearList = ArrayList()
                        response.data?.let { getEarthquake ->
                            earthquakeBodyRequest.userLat?.let { lat ->
                                earthquakeBodyRequest.userLong?.let { long ->
                                    filterNearList =
                                        getLocationFilterManuel(lat, long, getEarthquake)
                                }
                            }
                            this@MainViewModel.isNearPage.postValue(isNearPage)
                            getNearEarthquakeList.postValue(filterNearList)
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

    fun getLocationApi() {
        mainScope {
            showLoading()
            getLocationEarthquakeUseCase(location.value).collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getLocationEarthquake ->
                            getLocationApiEarthquakeList.postValue(getLocationEarthquake)
                            getNowEarthquakeList.postValue(getLocationEarthquake)
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

    fun getDataBetweenApi() {
        mainScope {
            showLoading()
            getDateBetweenEarthquakeUseCase(startDate.value, endDate.value)
                .collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let { getDateBetweenEarthquake ->
                                getDateEarthquakeList.postValue(getDateBetweenEarthquake)
                                getNowEarthquakeList.postValue(getDateBetweenEarthquake)
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

    fun getOnlyDataApi() {
        mainScope {
            showLoading()
            getOnlyDateEarthquakeUseCase(onlyDate.value).collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getOnlyDateEarthquake ->
                            getDateEarthquakeList.postValue(getOnlyDateEarthquake)
                            getNowEarthquakeList.postValue(getOnlyDateEarthquake)
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

    fun getThisWeekFilter() {
        mainScope {
            showLoading()

            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getEarthquake ->
                            getNowEarthquakeList.postValue(getEarthquake)
                        }
                        clickableHeaderMenus.postValue(true)
                        getNowEarthquakeList.value?.forEach { earthquake ->
                            filterNowList = getFilterOneWeek(earthquake)
                        }
                        getNowEarthquakeList.postValue(filterNowList)

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

    fun clearXmlData() {
        earthquakeBodyRequest = EarthquakeBodyRequest()
        location.value = ""
        ml.value = ""
        onlyDate.value = ""
        startDate.value = ""
        endDate.value = ""
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

    fun onClickFilter() {
        if (clickableHeaderMenus.value == true)
            clickFilter.postValue(true)
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
                getNearLocationFilter(false)
            else
                getLocationApi()
        } else if (selectedOption.value == 2) {
            if (subOption.value == true)
                getDataBetweenApi()
            else
                getOnlyDataApi()
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

    fun onCstmDatePickerStartDateClick() {
        clickCstmDatePickerStartDate.postValue(true)
    }

    fun onCstmDatePickerEndDateClick() {
        clickCstmDatePickerEndDate.postValue(true)
    }

    fun onCstmDatePickerOnlyDateClick() {
        clickCstmDatePickerOnlyDate.postValue(true)
    }

}