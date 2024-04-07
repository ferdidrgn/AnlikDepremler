package com.ferdidrgn.anlikdepremler.tools.helpers

import android.content.SharedPreferences
import com.google.gson.Gson
import com.securepreferences.SecurePreferences
import java.util.ArrayList
import java.util.HashSet

open class PreferencesManager {

    private var targetPreferences: SharedPreferences = SecurePreferences(
        com.ferdidrgn.anlikdepremler.AnlikDepremlerApp.inst.applicationContext,
        defaultPackageName,
        "APPLICATION_SHARED_PREFERENCES.xml"
    )

    private val gson = Gson()

    companion object {
        private const val defaultPackageName = "com.ferdidrgn.anlikdepremler"
        private const val emptyString = ""
    }

    fun getBooleanValue(key: String?): Boolean {
        return targetPreferences.getBoolean(key, false)
    }

    fun getBooleanValue(key: String?, defaultValue: Boolean): Boolean {
        return targetPreferences.getBoolean(key, defaultValue)
    }

    fun getFloat(key: String?, defaultValue: Float?): Float? {
        return targetPreferences.getFloat(key, 0f)
    }

    fun getInt(key: String?): Int {
        return try {
            targetPreferences.getInt(key, 0)
        } catch (e: Exception) {
            -1
        }
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return targetPreferences.getInt(key, defaultValue)
    }

    fun getLong(key: String?): Long {
        return targetPreferences.getLong(key, 0)
    }

    fun <T> getObject(key: String?, targetType: Class<T>?): T? {
        return getObject(key, targetType, null)
    }

    fun <T> getObject(key: String?, targetType: Class<T>?, defaultValue: T?): T? {
        if (targetPreferences.contains(key)) {
            val preferenceTarget = targetPreferences.getString(key, "")
            if ("" != preferenceTarget) {
                return gson.fromJson(preferenceTarget, targetType)
            }
        }
        return defaultValue
    }

    fun getString(key: String?): String? {
        return getString(key, emptyString)
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return targetPreferences.getString(key, defaultValue)
    }

    fun putBoolean(key: String?, value: Boolean): Boolean {
        val targetEditor = targetPreferences.edit()
        targetEditor.putBoolean(key, value)
        return targetEditor.commit()
    }

    fun putFloat(key: String?, targetValue: Float): Boolean {
        val targetEditor = targetPreferences.edit()
        return targetEditor.putFloat(key, targetValue).commit()
    }

    fun putFloat(key: String?, value: Float?) {
        val targetEditor = targetPreferences.edit()
        targetEditor.putString(key, value.toString())
        targetEditor.apply()
    }

    fun putInt(key: String?, targetValue: Int): Boolean {
        val targetEditor = targetPreferences.edit()
        return targetEditor.putInt(key, targetValue).commit()
    }

    fun putLong(key: String?, targetValue: Long): Boolean {
        val targetEditor = targetPreferences.edit()
        return targetEditor.putLong(key, targetValue).commit()
    }

    fun putObject(key: String?, targetObject: Any?) {
        putString(key, gson.toJson(targetObject))
    }

    fun putString(keys: Array<String?>, values: Array<String?>) {
        val targetEditor = targetPreferences.edit()
        var counter = 0
        for (key in keys) {
            targetEditor.putString(key, values[counter++])
        }
        targetEditor.apply()
    }

    fun saveToList(key: String?, list: ArrayList<String>) {
        val targetEditor = targetPreferences.edit()
        val set: MutableSet<String> = HashSet()
        set.addAll(list)
        targetEditor.putStringSet(key, set)
        targetEditor.apply()
    }

    fun getListOfString(key: String?): MutableSet<String>? {
        return targetPreferences.getStringSet(key, null)
    }

    fun putString(key: String?, value: String?) {
        val targetEditor = targetPreferences.edit()
        targetEditor.putString(key, value)
        targetEditor.apply()
    }
}