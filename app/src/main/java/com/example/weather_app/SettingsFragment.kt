package com.example.weather_app

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.weather_app.utils.SettingsManager
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsManager = SettingsManager(requireContext())

        view.findViewById<RadioGroup>(R.id.location_radio_group)
            .setOnCheckedChangeListener { _, checkedId ->
                val location = when (checkedId) {
                    R.id.radio_gps -> "GPS"
                    R.id.radio_map -> "Map"
                    else -> "GPS"
                }
                settingsManager.saveSelectedLocation(location)
            }

        view.findViewById<RadioGroup>(R.id.language_radio_group)
            .setOnCheckedChangeListener { _, checkedId ->
                val language = when (checkedId) {
                    R.id.radio_Eg -> {
                        local("en")
                        "en"
                    }

                    R.id.radio_Ar -> {
                        local("ar")
                        "ar"
                    }

                    else -> "en"
                }
                settingsManager.saveSelectedLanguage(language)
            }

        view.findViewById<RadioGroup>(R.id.Temperature_radio_group)
            .setOnCheckedChangeListener { _, checkedId ->
                val unit = when (checkedId) {
                    R.id.radio_K -> "standard"
                    R.id.radio_C -> "metric"
                    R.id.radio_F -> "imperial"
                    else -> "standard"
                }
                settingsManager.saveSelectedTemperatureUnit(unit)
            }

        view.findViewById<RadioGroup>(R.id.wind_speed_radio_group)
            .setOnCheckedChangeListener { _, checkedId ->
                val unit = when (checkedId) {
                    R.id.radio_miles_hour -> "Miles/hour"
                    R.id.radio_meter_sec -> "Meter/sec"
                    else -> "Meter/sec"
                }
                settingsManager.saveSelectedWindSpeedUnit(unit)
            }

        loadSavedPreferences(view)

        return view
    }

    private fun loadSavedPreferences(view: View) {
        val savedLocation = settingsManager.getSelectedLocation()
        if (savedLocation != null) {
            when (savedLocation) {
                "GPS" -> view.findViewById<RadioButton>(R.id.radio_gps).isChecked = true
                "Map" -> view.findViewById<RadioButton>(R.id.radio_map).isChecked = true
            }
        }

        val savedLanguage = settingsManager.getSelectedLanguage()
        if (savedLanguage != null) {
            when (savedLanguage) {
                "English" -> view.findViewById<RadioButton>(R.id.radio_Eg).isChecked = true
                "Arabic" -> view.findViewById<RadioButton>(R.id.radio_Ar).isChecked = true
            }
        }

        val savedTemperatureUnit = settingsManager.getSelectedTemperatureUnit()
        if (savedTemperatureUnit != null) {
            when (savedTemperatureUnit) {
                "Kelvin" -> view.findViewById<RadioButton>(R.id.radio_K).isChecked = true
                "Celsius" -> view.findViewById<RadioButton>(R.id.radio_C).isChecked = true
                "Fahrenheit" -> view.findViewById<RadioButton>(R.id.radio_F).isChecked = true
            }
        }

        val savedWindSpeedUnit = settingsManager.getSelectedWindSpeedUnit()
        if (savedWindSpeedUnit != null) {
            when (savedWindSpeedUnit) {
                "Miles/hour" -> view.findViewById<RadioButton>(R.id.radio_miles_hour).isChecked =
                    true

                "Meter/sec" -> view.findViewById<RadioButton>(R.id.radio_meter_sec).isChecked = true
            }
        }
    }

    private fun local(language: String) {
        val resources = resources
        val dm = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(language))
            config.setLayoutDirection(Locale(language))
        } else {
            config.locale = Locale(language)
        }
        resources.updateConfiguration(config, dm)
        requireActivity().recreate()
    }
}


