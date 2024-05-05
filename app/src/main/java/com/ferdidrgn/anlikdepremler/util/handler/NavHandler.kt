package com.ferdidrgn.anlikdepremler.util.handler

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import com.ferdidrgn.anlikdepremler.util.helpers.ToMain
import com.ferdidrgn.anlikdepremler.util.helpers.WhichTermsAndPrivacy
import com.ferdidrgn.anlikdepremler.presentation.filter.FilterActivity
import com.ferdidrgn.anlikdepremler.presentation.language.LanguageActivity
import com.ferdidrgn.anlikdepremler.presentation.main.MainActivity
import com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake.MapsEarthquakeActivity
import com.ferdidrgn.anlikdepremler.presentation.termsAndConditionsandPrivacePolicy.TermsAndConditionsAndPrivacyPolicyActivity
import com.ferdidrgn.anlikdepremler.util.helpers.NEAR_EARTHQUAKE
import com.ferdidrgn.anlikdepremler.util.helpers.TO_MAIN
import com.ferdidrgn.anlikdepremler.util.helpers.WHICH_TERMS_PRIVACY

class NavHandler {

    companion object {
        val instance = NavHandler()
    }

    fun toRestartApp(context: Context) {
        val packageManager = context.applicationContext.packageManager
        val intent =
            packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun toChangeTheme(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(TO_MAIN, ToMain.Settings)
        context.startActivity(intent)
    }

    fun toMainActivity(
        context: Context,
        toMain: ToMain,
        finishAffinity: Boolean = false,
        onlyClearTask: Boolean = false
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(TO_MAIN, toMain)

        if (finishAffinity)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        else if (onlyClearTask)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        else
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        context.startActivity(intent)

        if (finishAffinity)
            finishAffinity(context as AppCompatActivity)
    }

    fun toMapsActivity(
        context: Context,
        isNearEarthquake: Boolean?
    ) {
        val intent = Intent(context, MapsEarthquakeActivity::class.java)
        intent.putExtra(NEAR_EARTHQUAKE, isNearEarthquake)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun toLanguageActivity(context: Context) {
        val intent = Intent(context, LanguageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun toFilterActivity(context: Context) {
        val intent = Intent(context, FilterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun toTermsConditionsAndPrivacyPolicyActivity(
        context: Context,
        whichTermsAndPrivacy: WhichTermsAndPrivacy
    ) {
        val intent = Intent(context, TermsAndConditionsAndPrivacyPolicyActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(WHICH_TERMS_PRIVACY, whichTermsAndPrivacy)
        context.startActivity(intent)
    }

    fun toBuyCoffeeUrl(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://banabicoffee.com/@ferdidrgn")
        context.startActivity(intent)
    }

    fun toPhoneSettings(context: Context, activity: AppCompatActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}