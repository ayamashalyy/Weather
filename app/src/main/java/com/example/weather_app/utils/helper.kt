package com.example.weather_app.utils

import android.content.Context
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_LOCATION
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_TEMPERATURE_UNIT
import com.example.weather_app.utils.Constants.Companion.KEY_SELECTED_WIND_SPEED_UNIT
import com.example.weather_app.utils.Constants.Companion.PREF_NAME
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SettingsManager(context: Context) {


    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val _selectedTemperatureUnitFlow = MutableSharedFlow<String>()
    val selectedTemperatureUnitFlow = _selectedTemperatureUnitFlow.asSharedFlow()

    private val _selectedLanguageFlow = MutableSharedFlow<String>()
    val selectedLanguageFlow = _selectedLanguageFlow.asSharedFlow()

    private val _selectedWindSpeedUnitFlow = MutableSharedFlow<String?>()
    val selectedWindSpeedUnitFlow = _selectedWindSpeedUnitFlow.asSharedFlow()

    fun saveSelectedLocation(location: String) {
        editor.putString(KEY_SELECTED_LOCATION, location).apply()
    }

    fun getSelectedLocation(): String {
        return sharedPreferences.getString(KEY_SELECTED_LOCATION, "GPS")!!
    }

    fun saveSelectedLanguage(language: String) {
        if (language != getSelectedLanguage()) {
            editor.putString(KEY_SELECTED_LANGUAGE, language).apply()
            _selectedLanguageFlow.tryEmit(language)
        }
    }

    fun getSelectedLanguage(): String {
        return sharedPreferences.getString(KEY_SELECTED_LANGUAGE, "en")!!
    }

    fun saveSelectedTemperatureUnit(unit: String) {
        editor.putString(KEY_SELECTED_TEMPERATURE_UNIT, unit).apply()
        _selectedTemperatureUnitFlow.tryEmit(unit)
    }

    fun getSelectedTemperatureUnit(): String {
        return sharedPreferences.getString(
            KEY_SELECTED_TEMPERATURE_UNIT, "standard"
        )!!
    }

    fun saveSelectedWindSpeedUnit(unit: String) {
        editor.putString(KEY_SELECTED_WIND_SPEED_UNIT, unit).apply()
        _selectedWindSpeedUnitFlow.tryEmit(unit)
    }

    fun getSelectedWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_SELECTED_WIND_SPEED_UNIT, "m/s")
    }
}

