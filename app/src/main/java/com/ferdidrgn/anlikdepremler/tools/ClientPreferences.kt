package com.ferdidrgn.anlikdepremler.tools

import com.ferdidrgn.anlikdepremler.enums.ContextLanguages
import com.ferdidrgn.anlikdepremler.enums.Languages
import com.ferdidrgn.anlikdepremler.tools.helpers.PreferencesManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ClientPreferences : PreferencesManager() {

    companion object {
        lateinit var inst: ClientPreferences
    }

    var baseUrl: String?
        get() = getString(BASE_URL)
        set(value) {
            putString(BASE_URL, value)
        }

    var guestToken: String?
        get() = getString(GUEST_TOKEN, "")
        set(token) = putString(GUEST_TOKEN, token)

    var token: String?
        get() = getString(TOKEN, null)
        set(token) = putString(TOKEN, token)

    //for Onboarding
    var isFirstLaunch: Boolean?
        get() = getBooleanValue(IS_FIRST_LUNCH, true)
        set(isFirstLaunch) {
            putBoolean(IS_FIRST_LUNCH, isFirstLaunch ?: true)
        }

    var userLat: Float?
        get() = getFloat(USER_LAT, null)
        set(value) = putFloat(USER_LAT, value)

    var userLong: Float?
        get() = getFloat(USER_LONG, null)
        set(value) = putFloat(USER_LONG, value)

    var userLocation: String?
        get() = getString(USER_LOCATION, "")
        set(value) = putString(USER_LOCATION, value)

    var language: String
        get() = getString(LANGUAGE, Languages.TURKISH.language).toString()
        set(token) = putString(LANGUAGE, token)

    var contextLanguage: String
        get() = getString(CONTEXT_LANGUAGE, ContextLanguages.TURKISH.language).toString()
        set(token) = putString(CONTEXT_LANGUAGE, token)

    var isDarkMode: Boolean?
        get() = getBooleanValue(IS_DARK_MODE, false)
        set(isDarkMode) {
            putBoolean(IS_DARK_MODE, isDarkMode ?: false)
        }

    var reviewStatus: Boolean
        get() = getBooleanValue(REVIEW_STATUS, false)
        set(value) {
            putBoolean(REVIEW_STATUS, value)
        }

    var reviewCounter: Int
        get() = getInt(REVIEW_COUNT)
        set(value) {
            putInt(REVIEW_COUNT, value)
        }

}