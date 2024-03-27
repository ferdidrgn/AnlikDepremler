package com.ferdidrgn.anlikdepremler.enums

import android.content.Context
import androidx.annotation.StringRes
import com.ferdidrgn.anlikdepremler.R


enum class ToMain { Home, NearEarthquake, NowEarthquake, Settings }

enum class WhichTermsAndPrivacy { TermsAndCondition, PrivacyAndPolicy }

enum class Response { Empty, ServerError, ThereIs, NotEqual }
enum class Environment(val url: String) {
    test("http://furkansezgin.com.tr/"),
    preprod("http://furkansezgin.com.tr/"),
    prod("http://furkansezgin.com.tr/")
}

enum class Languages(val language: String) {
    TURKISH("tr_TUR"),
    English("en_US")
}

enum class ContextLanguages(val language: String) {
    TURKISH("tr"),
    English("en")
}

enum class ToolBarTitles(@field:StringRes @param:StringRes private val mLabel: Int) {
    Home(R.string.title_home),
    NearEarthquake(R.string.near_earthquake),
    NowEarthquake(R.string.now_earthquake),
    Settings(R.string.settings),
    Language(R.string.language);

    internal inner class Entry(context: Context) {
        private val mContext: Context
        override fun toString(): String {
            return mContext.getString(mLabel)
        }

        init {
            mContext = context
        }
    }
}