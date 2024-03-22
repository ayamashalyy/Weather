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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import com.example.weather_app.R
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
    private lateinit var city: TextView
    private lateinit var date: TextView
    private lateinit var dec: TextView
    private lateinit var temp: TextView
    private lateinit var image: ImageView
    private lateinit var humidity: TextView
    private lateinit var clouds: TextView
    private lateinit var pressure: TextView
    private lateinit var wind: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewday: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var hourAdapter: DayListAdapter
    private lateinit var dayAdapter: WeekListAdapter
    private lateinit var settingsManager: SettingsManager
    private lateinit var card:CardView
    private var unit: String = Constants.CELSIUE
    private var language: String = Constants.ARABIC
    private var localPermissionGPSCode = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        city = view.findViewById(R.id.city_name_txt)
        date = view.findViewById(R.id.date_txt)
        dec = view.findViewById(R.id.temp_desc)
        temp = view.findViewById(R.id.temp_txt)
        image = view.findViewById(R.id.weatherImg)
        humidity = view.findViewById(R.id.humidity_txt)
        clouds = view.findViewById(R.id.clouds_txt)
        pressure = view.findViewById(R.id.pressure_txt)
        wind = view.findViewById(R.id.wind_txt)
        recyclerView = view.findViewById(R.id.hourly_weather_recycler)
        recyclerViewday = view.findViewById(R.id.week_weather_recycler)
        card = view.findViewById(R.id.home_details_card)

        setUpRecyclerView()
        return view
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        hourAdapter = DayListAdapter(requireContext())
        recyclerView.adapter = hourAdapter
        recyclerView.layoutManager = linearLayoutManager

        val weekLinearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        dayAdapter = WeekListAdapter(requireContext())
        recyclerViewday.adapter = dayAdapter
        recyclerViewday.layoutManager = weekLinearLayoutManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsManager = SettingsManager(requireContext())
        applySavedSettings()

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
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]
        if (activeNetwork != null) {
            lifecycleScope.launch {
                viewModel._weather.collectLatest { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            card.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is ApiState.Success -> {
                            progressBar.visibility = View.GONE
                            card.visibility = View.VISIBLE
                            viewModel.insertCurrentWeatherInHome(result.data)
                            recyclerView.visibility = View.VISIBLE
                            val weather = result.data
                            city.text = weather.city.name
                            hourAdapter.submitList(weather.list)
                            dayAdapter.submitList(weather.list)
                            date.text = weather.list.firstOrNull()?.let {
                                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(
                                    SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dt_txt)
                                )
                            }
                            dec.text =
                                weather.list.firstOrNull()?.weather?.firstOrNull()?.description
                                    ?: "No description available"
                            val temperatureInCelsius = weather.list.firstOrNull()?.main?.temp
                            if (language.equals("en")) {
                                val temperatureString = when (unit) {
                                    "metric" -> "${temperatureInCelsius}\u2103"
                                    "standard" -> "${temperatureInCelsius}\u212A"
                                    "imperial" -> "${temperatureInCelsius}\u2109"
                                    else -> "${temperatureInCelsius}\u212A"
                                }
                                temp.text = temperatureString
                            } else {
                                val temperatureString = when (unit) {
                                    "metric" -> "${temperatureInCelsius}°س  "
                                    "standard" -> "${temperatureInCelsius}°ك "
                                    "imperial" -> "${temperatureInCelsius}°ف "
                                    else -> "${temperatureInCelsius}°ك "
                                }
                                temp.text = temperatureString
                            }
                            Glide.with(this@HomeFragment)
                                .load("https://openweathermap.org/img/wn/${weather.list[0].weather[0].icon}@2x.png")
                                .into(image)
                            humidity.text =
                                "${weather.list.firstOrNull()?.main?.humidity.toString()}%"
                            clouds.text = "${weather.list.firstOrNull()?.clouds?.all.toString()}%"
                            pressure.text =
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
                            recyclerView.visibility = View.VISIBLE
                            val weather = result.data
                            city.text = weather.city.name
                            hourAdapter.submitList(weather.list)
                            dayAdapter.submitList(weather.list)
                            date.text = weather.list.firstOrNull()?.let {
                                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(
                                    SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dt_txt)
                                )
                            }
                            dec.text =
                                weather.list.firstOrNull()?.weather?.firstOrNull()?.description
                                    ?: "No description available"
                            val temperatureInCelsius = weather.list.firstOrNull()?.main?.temp
                            if (language.equals("en")) {
                                val temperatureString = when (unit) {
                                    "metric" -> "${temperatureInCelsius}\u2103"
                                    "standard" -> "${temperatureInCelsius}\u212A"
                                    "imperial" -> "${temperatureInCelsius}\u2109"
                                    else -> "${temperatureInCelsius}\u212A"
                                }
                                temp.text = temperatureString
                            } else {
                                val temperatureString = when (unit) {
                                    "metric" -> "${temperatureInCelsius}°س  "
                                    "standard" -> "${temperatureInCelsius}°ك "
                                    "imperial" -> "${temperatureInCelsius}°ف "
                                    else -> "${temperatureInCelsius}°ك "
                                }
                                temp.text = temperatureString
                            }

                            Glide.with(this@HomeFragment)
                                .load("https://openweathermap.org/img/wn/${weather.list[0].weather[0].icon}@2x.png")
                                .into(image)
                            humidity.text =
                                "${weather.list.firstOrNull()?.main?.humidity.toString()}%"
                            clouds.text = "${weather.list.firstOrNull()?.clouds?.all.toString()}%"
                            pressure.text =
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

        getLocation()
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
                            Log.i("TAG", "$language ")
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
        language = settingsManager.getSelectedLanguage().toString()
        unit = settingsManager.getSelectedTemperatureUnit().toString()
        Log.i("TAG", "$language ")
        if (unit != unit) {
            unit = unit
            updateTemperatureDisplay()
        }
    }

    private fun updateTemperatureDisplay() {
        val temperatureInCelsius = temp.text.toString().replace("°C", "").toInt()
        if (language.equals("en")) {
            val temperatureString = when (unit) {
                "metric" -> "${temperatureInCelsius}\u2103"
                "standard" -> "${temperatureInCelsius}\u212A"
                "imperial" -> "${temperatureInCelsius}\u2109"
                else -> "${temperatureInCelsius}\u212A"
            }
            temp.text = temperatureString
        } else {
            val temperatureString = when (unit) {
                "metric" -> "${temperatureInCelsius}°س  "
                "standard" -> "${temperatureInCelsius}°ك "
                "imperial" -> "${temperatureInCelsius}°ف "
                else -> "${temperatureInCelsius}°ك "
            }
            temp.text = temperatureString
        }
    }

    private fun updateWindSpeedDisplay(weather: WeatherResponse) {
        val windSpeedUnit = settingsManager.getSelectedWindSpeedUnit()
        val windSpeed = weather.list.firstOrNull()?.wind?.speed ?: 0.0
        val formattedWindSpeed = when (windSpeedUnit) {
            Constants.METRIC_WIND_SPEED_UNIT -> "${windSpeed} m/h"
            Constants.MILES_WIND_SPEED_UNIT -> "${windSpeed} m/s"

            else -> "${windSpeed} m/s"
        }
        wind.text = formattedWindSpeed
    }


}
