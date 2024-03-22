package com.example.weather_app.utils

import android.content.Context
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_LOCATION
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_TEMPERATURE_UNIT
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_WIND_SPEED_UNIT
import com.example.weather_app.utils.Constants.Companion.PREF_NAME

class SettingsManager(context: Context) {


    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun saveSelectedLocation(location: String) {
        editor.putString(KEY_SELECTED_LOCATION, location).apply()
    }

    fun getSelectedLocation(): String {
        return sharedPreferences.getString(KEY_SELECTED_LOCATION, null)!!
    }

    fun saveSelectedLanguage(language: String) {
        editor.putString(KEY_SELECTED_LANGUAGE, language).apply()
    }

    fun getSelectedLanguage(): String {
        return sharedPreferences.getString(KEY_SELECTED_LANGUAGE, "en")!!
    }

    fun saveSelectedTemperatureUnit(unit: String) {
        editor.putString(KEY_SELECTED_TEMPERATURE_UNIT, unit).apply()
    }

    fun getSelectedTemperatureUnit(): String {
        return sharedPreferences.getString(KEY_SELECTED_TEMPERATURE_UNIT, "standard")!!
    }

    fun saveSelectedWindSpeedUnit(unit: String) {
        editor.putString(KEY_SELECTED_WIND_SPEED_UNIT, unit).apply()
    }

    fun getSelectedWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_SELECTED_WIND_SPEED_UNIT, null)
    }
}

