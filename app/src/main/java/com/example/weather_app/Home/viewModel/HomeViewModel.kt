package com.example.weather_app.Home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.Model.WeatherRepository
import com.example.weather_app.Model.WeatherResponse
import android.util.Log

class HomeViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _weather: MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> get() = _weather

    suspend fun getMyWeatherStatus(latitude: Double, longitude: Double, language: String, units: String) {
        try {
            val weatherData = repo.getWeather(latitude, longitude, language, units)
            _weather.postValue(weatherData)
        } catch (e: Exception) {
            Log.e("com.example.weather_app.Home.viewModel.HomeViewModel", "Error fetching weather data", e)
        }
    }
}
