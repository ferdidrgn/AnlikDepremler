package com.ferdidrgn.anlikdepremler.presentation.main.settings

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.util.helpers.Response
import com.ferdidrgn.anlikdepremler.domain.useCase.contactUsEmail.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.util.helpers.ClientPreferences
import com.ferdidrgn.anlikdepremler.util.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getContactUsEmailUseCase: GetContactUsEmailUseCase,
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
    val btnPrivacyPolicyClicked = LiveEvent<Boolean>()
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
            getContactUsEmailUseCase { status, contactUs ->
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

    fun onPrivacyPolicyClick() {
        btnPrivacyPolicyClicked.postValue(true)
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