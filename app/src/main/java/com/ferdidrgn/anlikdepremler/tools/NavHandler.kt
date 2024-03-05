package com.ferdidrgn.anlikdepremler.tools

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import com.ferdidrgn.anlikdepremler.enums.ToMain
import com.ferdidrgn.anlikdepremler.enums.WhichTermsAndPrivace
import com.ferdidrgn.anlikdepremler.model.Earthquake
import com.ferdidrgn.anlikdepremler.ui.language.LanguageActivity
import com.ferdidrgn.anlikdepremler.ui.main.MainActivity
import com.ferdidrgn.anlikdepremler.ui.main.mapsEartquake.MapsEarthquakeActivity
import com.ferdidrgn.anlikdepremler.ui.termsAndConditionsandPrivacePolicy.TermsAndConditionsandPrivacePolicyActivity

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

    fun toMapsActivity(
        context: Context,
        filterList: ArrayList<Earthquake>,
        isNearEarthquake: Boolean
    ) {
        val intent = Intent(context, MapsEarthquakeActivity::class.java)
        intent.putExtra(NEAR_EARTHQUAKE, isNearEarthquake)
        intent.putExtra(FILTER_LIST, filterList)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun toMainActivityFinishAffinity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        finishAffinity(context as AppCompatActivity)
    }

    fun toMainActivity(context: Context, toMain: ToMain) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(TO_MAIN, toMain)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun toMainActivityClearTask(context: Context, toMain: ToMain) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(NAVIGATION_KEY, toMain)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(intent)
    }

    fun toLanguageActivity(context: Context) {
        val intent = Intent(context, LanguageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun toTermsConditionsAndPrivacePolicyActivity(
        context: Context,
        whichTermsAndPrivace: WhichTermsAndPrivace
    ) {
        val intent = Intent(context, TermsAndConditionsandPrivacePolicyActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(WHICH_TERMS_PRIVACE, whichTermsAndPrivace)
        context.startActivity(intent)
    }
}