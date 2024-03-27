package com.example.weather_app.Home.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.ApiState
import com.example.weather_app.Home.viewModel.HomeViewModel
import com.example.weather_app.Home.viewModel.HomeViewModelFactory
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.Model.WeatherResponse
import com.example.weather_app.databinding.FragmentHomeBinding
import com.example.weather_app.utils.SettingsManager
import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp
import com.example.weather_app.utils.Constants
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

open class HomeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourAdapter: DayListAdapter
    private lateinit var dayAdapter: WeekListAdapter
    private lateinit var settingsManager: SettingsManager
    private var unit: String = Constants.CELSIUE
    private var language: String = Constants.ARABIC
    private var localPermissionGPSCode = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        settingsManager = SettingsManager(requireContext())
        applySavedSettings()
        val sharedPreferences =
            requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getString("latitude", "0")?.toDouble() ?: 0.0
        val longitude = sharedPreferences.getString("longitude", "0")?.toDouble() ?: 0.0
        Log.i("Trace Geolocation", "ddddddddddddddd lat: ${latitude} ==> lan: ${longitude}")
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val homeFactory = HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                WeatherLocalDataSourceImp.getInstance(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]

        if (latitude != 0.0 && longitude != 0.0) {
            lifecycleScope.launch {
                viewModel.getMyWeatherStatus(latitude, longitude, unit, language)
            }
        } else {
            getLocation()
        }

        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE
        if (activeNetwork != null) {
            lifecycleScope.launch {
                viewModel._weather.collectLatest { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            binding.homeDetailsCard.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is ApiState.Success -> {
                            progressBar.visibility = View.GONE
                            binding.homeDetailsCard.visibility = View.VISIBLE
                            viewModel.insertCurrentWeatherInHome(result.data)
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

                            Glide.with(this@HomeFragment)
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
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(), "Error loading weather data", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                viewModel.getCurrentWeatherInHome()
                viewModel._weather.collectLatest { result ->
                    when (result) {
                        is ApiState.Loading -> {
                        }

                        is ApiState.Success -> {
                            viewModel.insertCurrentWeatherInHome(result.data)
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

                            Glide.with(this@HomeFragment)
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
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                localPermissionGPSCode
            )
        } else {
            val locationRequest = createLocationRequest()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0?.let {
                        for (location in it.locations) {
                            lifecycleScope.launch {
                                viewModel.getMyWeatherStatus(
                                    location.latitude, location.longitude, unit, language

                                )
                            }
                        }
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == localPermissionGPSCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
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
