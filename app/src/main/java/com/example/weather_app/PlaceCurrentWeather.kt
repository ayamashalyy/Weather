package com.example.weather_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.Home.view.DayListAdapter
import com.example.weather_app.Home.view.WeekListAdapter
import com.example.weather_app.Home.viewModel.HomeViewModel
import com.example.weather_app.Home.viewModel.HomeViewModelFactory
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.Model.WeatherResponse
import com.example.weather_app.databinding.FragmentHomeBinding
import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.SettingsManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

open class PlaceCurrentWeather : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourAdapter: DayListAdapter
    private lateinit var dayAdapter: WeekListAdapter
    private var unit: String = Constants.CELSIUE
    private var language: String = Constants.ARABIC
    private lateinit var settingsManager: SettingsManager

    var long: Double? = 0.0
    var lat: Double? = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        settingsManager = SettingsManager(requireContext())
        applySavedSettings()
        val bundle = arguments
        if (bundle != null) {
            val data = bundle.getParcelable<FavLocation>("cardViewId")
            long = data?.longitude
            lat = data?.latitude
        }

        val homeFactory = HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                WeatherLocalDataSourceImp.getInstance(requireContext())
            )
        )
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this, homeFactory).get(HomeViewModel::class.java)
        lifecycleScope.launch {
            viewModel.getMyWeatherStatus(lat!!, long!!, unit, language)
            viewModel._weather.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        binding.homeDetailsCard.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
                    }

                    is ApiState.Success -> {
                        progressBar.visibility = View.GONE
                        binding.homeDetailsCard.visibility = View.VISIBLE
                        binding.hourlyWeatherRecycler.visibility = View.VISIBLE
                        val weather = result.data
                        binding.cityNameTxt.text = weather.city.name
                        hourAdapter.submitList(weather.list)
                        hourAdapter.updateUnit(unit)
                        dayAdapter.submitList(weather.list)
                        dayAdapter.updateUnit(unit)
                        binding.dateTxt.text = weather.list.firstOrNull()?.let {
                            SimpleDateFormat(
                                "dd MMM yyyy", Locale(settingsManager.getSelectedLanguage())
                            ).format(
                                SimpleDateFormat(
                                    "yyyy-MM-dd", Locale(settingsManager.getSelectedLanguage())
                                ).parse(it.dt_txt)
                            )
                        }
                        binding.tempDesc.text =
                            weather.list.firstOrNull()?.weather?.firstOrNull()?.description
                                ?: "No description available"
                        val temperatureInCelsius = weather.list.firstOrNull()?.main?.temp
                        val temperatureString = when (unit) {
                            "metric" -> "${temperatureInCelsius}\u2103"
                            "standard" -> "${temperatureInCelsius}\u212A"
                            "imperial" -> "${temperatureInCelsius}\u2109"
                            else -> "${temperatureInCelsius}\u212A"
                        }
                        binding.tempTxt.text = temperatureString
                        Glide.with(this@PlaceCurrentWeather)
                            .load("https://openweathermap.org/img/wn/${weather.list[0].weather[0].icon}@2x.png")
                            .into(binding.weatherImg)
                        binding.humidityTxt.text =
                            "${weather.list.firstOrNull()?.main?.humidity.toString()}%"
                        binding.cloudsTxt.text =
                            "${weather.list.firstOrNull()?.clouds?.all.toString()}%"
                        binding.pressureTxt.text =
                            "${weather.list.firstOrNull()?.main?.pressure.toString()}hpa"
                        updateWindSpeedDisplay(weather)

                    }

                    else -> {
                        Toast.makeText(
                            requireContext(), "Error loading weather data", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun setUpRecyclerView() {
        binding.hourlyWeatherRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        hourAdapter = DayListAdapter(requireContext())
        binding.hourlyWeatherRecycler.adapter = hourAdapter

        binding.weekWeatherRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        dayAdapter = WeekListAdapter(requireContext())
        binding.weekWeatherRecycler.adapter = dayAdapter
    }

    private fun applySavedSettings() {
        language = settingsManager.getSelectedLanguage()
        unit = settingsManager.getSelectedTemperatureUnit()
        Log.i("TAG", "$language ")
        if (unit != unit) {
            unit = unit
            updateTemperatureDisplay()
        }
    }

    private fun updateTemperatureDisplay() {
        val temperatureInCelsius = binding.tempTxt.text.toString().replace("°C", "").toInt()
        val temperatureString = when (unit) {
            "metric" -> "${temperatureInCelsius}\u2103"
            "standard" -> "${temperatureInCelsius}\u212A"
            "imperial" -> "${temperatureInCelsius}\u2109"
            else -> "${temperatureInCelsius}\u212A"
        }
        binding.tempTxt.text = temperatureString
    }


    private fun updateWindSpeedDisplay(weather: WeatherResponse) {
        val windSpeedUnit = settingsManager.getSelectedWindSpeedUnit()
        val windSpeed = weather.list.firstOrNull()?.wind?.speed ?: 0.0
        val formattedWindSpeed = windSpeedUnit?.let { convertWindSpeed(windSpeed, it) }
        binding.windTxt.text = formattedWindSpeed
    }

    private fun convertWindSpeed(speed: Double, unit: String): String {
        return when (unit) {
            Constants.METRIC_WIND_SPEED_UNIT -> "${speed} m/h"
            Constants.MILES_WIND_SPEED_UNIT -> "${speed * 2} m/s"
            else -> "${speed} m/s"
        }
    }


}