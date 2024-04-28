package com.ferdidrgn.anlikdepremler.presentation.ui.filter

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.model.dummyModel.EarthquakeBodyRequest
import com.ferdidrgn.anlikdepremler.domain.useCase.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetOnlyDateEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.presentation.ui.mapsEarthquake.NowEarthQuakeAdapterListener
import com.ferdidrgn.anlikdepremler.tools.checkBetweenData
import com.ferdidrgn.anlikdepremler.tools.checkMapManuelStatus
import com.ferdidrgn.anlikdepremler.tools.getAllFilterQueriers
import com.ferdidrgn.anlikdepremler.tools.getLocationFilterManuel
import com.ferdidrgn.anlikdepremler.tools.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.tools.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getEarthquakeUseCase: GetEarthquakeUseCase,
    private val getOnlyDateEarthquakeUseCase: GetOnlyDateEarthquakeUseCase,
    private val getDateBetweenEarthquakeUseCase: GetDateBetweenEarthquakeUseCase,
    private val getLocationEarthquakeUseCase: GetLocationEarthquakeUseCase
) : BaseViewModel(), NowEarthQuakeAdapterListener {

    private var job: Job? = null

    //Get Api List
    var getNowEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getNowFilterList = MutableLiveData<ArrayList<Earthquake>?>()
    var getDateEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getLocationApiEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()

    //XML ve filter
    var earthquakeBodyRequest = EarthquakeBodyRequest()
    var location = MutableStateFlow("")
    var ml = MutableStateFlow("")
    var startDate = MutableStateFlow("")
    var endDate = MutableStateFlow("")
    var onlyDate = MutableStateFlow("")

    val selectedOption = MutableLiveData<Int>()
    val subOption = MutableLiveData(false)

    var filterNowList = ArrayList<Earthquake>()
    var filterNearList = ArrayList<Earthquake>()

    var isNearPage = MutableLiveData(false)

    var userLat = MutableStateFlow<Double?>(null)
    var userLong = MutableStateFlow<Double?>(null)

    //XML Click
    val clickApply = LiveEvent<Boolean>()
    val clickFilter = LiveEvent<Boolean>()
    val clickFilterClear = LiveEvent<Boolean>()
    val clickClose = LiveEvent<Boolean>()
    val clickList = LiveEvent<Boolean>()
    val clickMap = LiveEvent<Boolean>()

    val clickCstmDatePickerStartDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerEndDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerOnlyDate = LiveEvent<Boolean?>()


    init {
        selectedOption.value = -1
        getNowEarthquake()
    }

    private fun getNowEarthquake() {
        mainScope {
            showLoading()

            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getEarthquake ->
                            getNowEarthquakeList.postValue(getEarthquake)
                            getNowFilterList.postValue(getEarthquake)
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

    private fun getDataBetweenApi() {
        mainScope {
            showLoading()
            getDateBetweenEarthquakeUseCase(startDate.value, endDate.value)
                .collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let { getDateBetweenEarthquake ->
                                getDateEarthquakeList.postValue(getDateBetweenEarthquake)
                                getNowFilterList.postValue(getDateBetweenEarthquake)
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
                            getNowFilterList.postValue(getLocationEarthquake)
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

    private fun getOnlyDataApi() {
        mainScope {
            showLoading()
            getOnlyDateEarthquakeUseCase(onlyDate.value).collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getOnlyDateEarthquake ->
                            getDateEarthquakeList.postValue(getOnlyDateEarthquake)
                            getNowFilterList.postValue(getOnlyDateEarthquake)
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
                        getNowFilterList.postValue(filterNowList)
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
            location = this@FilterViewModel.location.value
            ml = this@FilterViewModel.ml.value
            onlyDate = this@FilterViewModel.onlyDate.value
            startDate = this@FilterViewModel.startDate.value
            endDate = this@FilterViewModel.endDate.value

            if (ml.isEmpty() && !checkMapManuelStatus(userLat ?: 0.0, userLong ?: 0.0) &&
                !checkBetweenData(startDate, endDate) && onlyDate.isEmpty()
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
            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        getNowFilterList.postValue(null)
                        response.data?.let { getEarthquake ->
                            filterNearList =
                                earthquakeBodyRequest.userLat?.let { lat ->
                                    earthquakeBodyRequest.userLong?.let { long ->
                                        getLocationFilterManuel(lat, long, getEarthquake)
                                    }
                                } ?: ArrayList()

                            this@FilterViewModel.isNearPage.postValue(isNearPage)
                            getNowFilterList.postValue(filterNearList)
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

    fun clearXmlData() {
        earthquakeBodyRequest = EarthquakeBodyRequest()
        location.value = ""
        ml.value = ""
        onlyDate.value = ""
        startDate.value = ""
        endDate.value = ""
    }


    //Click Events
    fun onClickMap() {
        clickMap.postValue(true)
    }

    fun onClickClear() {
        clickFilterClear.postValue(true)
    }

    fun onClickList() {
        clickList.postValue(true)
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