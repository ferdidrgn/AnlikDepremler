package com.ferdidrgn.anlikdepremler.presentation.filter

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.util.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.util.base.Resource
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake
import com.ferdidrgn.anlikdepremler.domain.useCase.dataBetweenEarthquake.GetDateBetweenEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.earthquakes.GetEarthquakesUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.locationEarthquake.GetLocationEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.domain.useCase.dataOnlyEarthquake.GetOnlyDateEarthquakeUseCase
import com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake.NowEarthQuakeAdapterListener
import com.ferdidrgn.anlikdepremler.util.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getEarthquakesUseCase: GetEarthquakesUseCase,
    private val getDateBetweenEarthquakeUseCase: GetDateBetweenEarthquakeUseCase,
    private val getLocationEarthquakeUseCase: GetLocationEarthquakeUseCase,
    private val getOnlyDateEarthquakeUseCase: GetOnlyDateEarthquakeUseCase
) : BaseViewModel(), NowEarthQuakeAdapterListener {

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

    var userLat = MutableStateFlow<Double?>(null)
    var userLong = MutableStateFlow<Double?>(null)

    //XML Click
    val clickFilterClear = LiveEvent<Boolean>()

    val clickCstmDatePickerStartDate = LiveEvent<Boolean?>()
    val clickCstmDatePickerEndDate = LiveEvent<Boolean?>()

    init {
        getNowEarthquake()
    }

    private fun getNowEarthquake() {
        mainScope {
            showLoading()

            getEarthquakesUseCase().collectLatest { response ->
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

    private fun getMlFilter() {
        mainScope {
            showLoading()

            getOnlyMlFilter()

            hideLoading()
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

        if (location.value.isEmpty() && startDate.value.isEmpty() && endDate.value.isEmpty() && ml.value.isEmpty())
            getNowEarthquake()

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