package com.example.weather_app

import android.content.Context
import com.example.weather_app.Map.view.MapSettings
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.weather_app.Home.view.HomeFragment
import com.example.weather_app.utils.SettingsManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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
                    R.id.radio_gps -> {
                        if (settingsManager.getSelectedLocation() != "GPS") {
                            val sharedPreferences = requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("latitude", "0").apply()
                            sharedPreferences.edit().putString("longitude", "0").apply()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, HomeFragment())
                                .commit()
                            settingsManager.saveSelectedLocation("GPS")
                        }
                        "GPS"
                    }
                    R.id.radio_map -> {
                        if (settingsManager.getSelectedLocation() != "Map") {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, MapSettings())
                                .commit()
                            settingsManager.saveSelectedLocation("Map")
                        }
                        "Map"
                    }
                    else -> "GPS"
                }
            }


        view.findViewById<RadioGroup>(R.id.language_radio_group)
            .setOnCheckedChangeListener { _, checkedId ->
                val language = when (checkedId) {
                    R.id.radio_Eg -> { if (settingsManager.getSelectedLocation() != "en") {
                        local("en")
                        settingsManager.saveSelectedLanguage("en")
                    }
                        "en"

                    }
                    R.id.radio_Ar -> {
                        if (settingsManager.getSelectedLocation() != "ar") {
                            local("ar")
                            settingsManager.saveSelectedLanguage("ar")

                        }
                        "ar"

                    }
                    else -> "en"
                }
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
                    R.id.radio_miles_hour -> "m/h"
                    R.id.radio_meter_sec -> "m/s"
                    else -> "m/s"
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

        lifecycleScope.launch {
            settingsManager.selectedLanguageFlow.collect { language ->
                when (language) {
                    "en" -> view.findViewById<RadioButton>(R.id.radio_Eg).isChecked = true
                    "ar" -> view.findViewById<RadioButton>(R.id.radio_Ar).isChecked = true
                }
            }
        }

        lifecycleScope.launch {
            settingsManager.selectedTemperatureUnitFlow
                .distinctUntilChanged()
                .collect { unit ->
                    when (unit) {
                        "standard" -> view.findViewById<RadioButton>(R.id.radio_K).isChecked = true
                        "metric" -> view.findViewById<RadioButton>(R.id.radio_C).isChecked = true
                        "imperial" -> view.findViewById<RadioButton>(R.id.radio_F).isChecked = true
                    }
                }
        }

       lifecycleScope.launch {
            settingsManager.selectedWindSpeedUnitFlow.collect { unit ->
                when (unit) {
                    "m/h" -> view.findViewById<RadioButton>(R.id.radio_miles_hour).isChecked = true
                    "m/s" -> view.findViewById<RadioButton>(R.id.radio_meter_sec).isChecked = true
                }
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

    }

}


