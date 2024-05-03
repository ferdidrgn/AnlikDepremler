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
    var getNowEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    private var getDateEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    private var getLocationApiEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()
    private var mlEarthquakeList = MutableLiveData<ArrayList<Earthquake>?>()

    //XML ve filter
    var location = MutableStateFlow("")
    var ml = MutableStateFlow("")
    var startDate = MutableStateFlow("")
    var endDate = MutableStateFlow("")
    var onlyDate = MutableStateFlow("")

    private var filterNearLocationList = ArrayList<Earthquake>()

    var userLat = MutableStateFlow<Double?>(null)
    var userLong = MutableStateFlow<Double?>(null)

    //XML Click
    val clickFilterClear = LiveEvent<Boolean>()
    val clickMap = LiveEvent<Boolean>()

    val clickCstmDatePickerStartDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerEndDate = LiveEvent<Boolean?>()


    init {
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

    /*private fun getNearLocationFilter() {
        job = mainScope {
            showLoading()
            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        getNowEarthquakeList.postValue(null)
                        response.data?.let { getEarthquake ->
                            filterNearLocationList =
                                userLat.value?.let { lat ->
                                    userLong.value?.let { long ->
                                        getLocationFilterManuel(lat, long, getEarthquake)
                                    }
                                } ?: ArrayList()

                            getNowEarthquakeList.postValue(filterNearLocationList)
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
    }*/

    private fun getDateBetweenApi() {
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

    /*private fun getOnlyDateApi() {
        mainScope {
            showLoading()
            getOnlyDateEarthquakeUseCase(onlyDate.value).collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getOnlyDateEarthquake ->
                            getDateEarthquakeList.postValue(getOnlyDateEarthquake)
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
    }*/

    private fun getMlFilter() {
        mainScope {
            showLoading()

            getEarthquakeUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { getEarthquake ->
                            getNowEarthquakeList.postValue(getEarthquake)
                        }
                        getOnlyMlFilter()
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

    private fun getOnlyMlFilter() {
        val filterList = ArrayList<Earthquake>()

        getNowEarthquakeList.value?.forEach { earthquake ->
            if (earthquake.ml.isNullOrEmpty().not())
                if (earthquake.ml?.toDoubleOrNull()!! > ml.value.toDoubleOrNull()!!)
                    filterList.add(earthquake)
        }
        mlEarthquakeList.postValue(filterList)
        getNowEarthquakeList.postValue(filterList)
    }

    fun clearXmlData() {
        location.value = ""
        ml.value = ""
        onlyDate.value = ""
        startDate.value = ""
        endDate.value = ""
    }


    //Click Events

    fun onClickApply() {

        if (location.value.isNotEmpty())
            getLocationApi()

        if (startDate.value.isNotEmpty() && endDate.value.isNotEmpty())
            getDateBetweenApi()

        if (ml.value.isNotEmpty())
            getMlFilter()
    }

    fun onClickMap() {
        clickMap.postValue(true)
    }

    fun onClickClear() {
        clickFilterClear.postValue(true)
    }

    fun onCstmDatePickerStartDateClick() {
        clickCstmDatePickerStartDate.postValue(true)
    }

    fun onCstmDatePickerEndDateClick() {
        clickCstmDatePickerEndDate.postValue(true)
    }

}