package com.ferdidrgn.anlikdepremler.presentation.ui.filter

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.useCase.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.GetOnlyDateEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.presentation.ui.mapsEarthquake.NowEarthQuakeAdapterListener
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
    private var getNowEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    var getNowFilterList = MutableLiveData<ArrayList<Earthquake>?>()
    private var getDateEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    private var getLocationApiEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()

    //XML ve filter
    var location = MutableStateFlow("")
    var ml = MutableStateFlow("")
    var startDate = MutableStateFlow("")
    var endDate = MutableStateFlow("")
    var onlyDate = MutableStateFlow("")

    val selectedOption = MutableLiveData<Int>()
    val subOption = MutableLiveData(false)

    var filterNowList = ArrayList<Earthquake>()
    private var filterNearLocationList = ArrayList<Earthquake>()

    var userLat = MutableStateFlow<Double?>(null)
    var userLong = MutableStateFlow<Double?>(null)

    //XML Click
    val clickApply = LiveEvent<Boolean>()
    val clickFilterClear = LiveEvent<Boolean>()
    val clickMap = LiveEvent<Boolean>()

    val clickCstmDatePickerStartDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerEndDate = LiveEvent<Boolean?>()


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

    private fun getLocationApi() {
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

    private fun getMlFilter() {
        mainScope {
            showLoading()

            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getEarthquake ->
                            getNowEarthquakeList.postValue(getEarthquake)
                        }
                        filterNowList = getOnlyMlFilter() ?: ArrayList()
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

    private fun getOnlyMlFilter(): ArrayList<Earthquake>? {
        val filterList = ArrayList<Earthquake>()

        getNowEarthquakeList.value?.forEach { earthquake ->
            if (earthquake.ml.isNullOrEmpty().not())
                if (earthquake.ml?.toDoubleOrNull()!! > ml.value.toDoubleOrNull()!!)
                    filterList.add(earthquake)
        }
        return filterList
    }

    private fun getNearLocationFilter() {
        job = mainScope {
            showLoading()
            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        getNowFilterList.postValue(null)
                        response.data?.let { getEarthquake ->
                            filterNearLocationList =
                                userLat.value?.let { lat ->
                                    userLong.value?.let { long ->
                                        getLocationFilterManuel(lat, long, getEarthquake)
                                    }
                                } ?: ArrayList()

                            getNowFilterList.postValue(filterNearLocationList)
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

    fun onClickApply() {
        clickApply.postValue(true)

        if (location.value.isNotEmpty())
            getLocationApi()
        else
            getNearLocationFilter()

        if (startDate.value.isNotEmpty() && endDate.value.isNotEmpty())
            getDataBetweenApi()
        else
            getOnlyDataApi()

        if (ml.value.isNotEmpty())
            getMlFilter()
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

}