package com.ferdidrgn.anlikdepremler

import android.app.Application
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnlikDepremlerApp : Application(){

    companion object{
        var inst = AnlikDepremlerApp()
    }

    override fun onCreate() {
        super.onCreate()
        inst = this
        ClientPreferences.inst = ClientPreferences()
    }
}