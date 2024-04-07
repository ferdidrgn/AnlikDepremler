package com.ferdidrgn.anlikdepremler.presentation.ui.main.settings

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.tools.enums.Response
import com.ferdidrgn.anlikdepremler.data.repositroy.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.tools.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appToolsFireBaseQueriesRepository: AppToolsFireBaseQueriesRepository
) : BaseViewModel() {

    val userLat = MutableStateFlow(0.0)
    val userLong = MutableStateFlow(0.0)
    val userLocation = MutableStateFlow("")

    //Click Events
    val btnLanguageClicked = LiveEvent<Boolean>()
    val btnOnShareAppClick = LiveEvent<Boolean>()
    val btnNotificationPermission = LiveEvent<Boolean>()
    val btnRateAppClicked = LiveEvent<Boolean>()
    val btnContactUsClicked = LiveEvent<Boolean>()
    val btnChangeThemeClicked = LiveEvent<Boolean>()
    val btnPrivacePolicyClicked = LiveEvent<Boolean>()
    val btnTermsAndConditionsClicked = LiveEvent<Boolean>()
    val btnBuyCoffeeUrlClick = LiveEvent<Boolean>()
    val btnBuyCoffeeGooglePlayClick = LiveEvent<Boolean>()

    init {
        ClientPreferences.inst.apply {
            if (userLat != null && userLong != null) {
                this@SettingsViewModel.userLat.value = this.userLat?.toDouble()!!
                this@SettingsViewModel.userLong.value = this.userLong?.toDouble()!!
                this@SettingsViewModel.userLocation.value = this.userLocation.toString()
            }
        }
    }

    fun getContactUs(): String {
        var valueReturn = ""
        mainScope {
            showLoading()
            appToolsFireBaseQueriesRepository.getContactUsEmail { status, contactUs ->
                when (status) {
                    Response.ThereIs -> {
                        contactUs?.let { data ->
                            valueReturn = data
                        }
                        hideLoading()
                    }

                    Response.Empty -> {
                        errorMessage.postValue(message(R.string.error_server))
                        hideLoading()
                    }

                    Response.ServerError -> {
                        errorMessage.postValue(message(R.string.error_message))
                        hideLoading()
                    }

                    else -> {
                        errorMessage.postValue(message(R.string.error_message))
                        hideLoading()
                    }
                }
            }
        }
        return valueReturn
    }

    //Click Events

    fun onLanguageClick() {
        btnLanguageClicked.postValue(true)
    }

    fun onShareAppClick() {
        btnOnShareAppClick.postValue(true)
    }

    fun onNotificationPermissionClick() {
        btnNotificationPermission.postValue(true)
    }

    fun onRateAppClick() {
        btnRateAppClicked.postValue(true)
    }

    fun onContactUsClick() {
        btnContactUsClicked.postValue(true)
    }

    fun onChangeThemeClick() {
        btnChangeThemeClicked.postValue(true)
    }

    fun onPrivacePolicyClick() {
        btnPrivacePolicyClicked.postValue(true)
    }

    fun onTermsAndConditionsClick() {
        btnTermsAndConditionsClicked.postValue(true)
    }

    fun onBuyCoffeeUrlClick() {
        btnBuyCoffeeUrlClick.postValue(true)
    }

    fun onBuyCoffeeGooglePlayClick() {
        btnBuyCoffeeGooglePlayClick.postValue(true)
    }
}