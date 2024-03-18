package com.example.weather_app.Home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.Model.WeatherRepository
import com.example.weather_app.Model.WeatherResponse
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weather_app.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepository) : ViewModel() {
    val _weather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)

    suspend fun getMyWeatherStatus(latitude: Double, longitude: Double, language: String, units: String) {
        viewModelScope.launch {
            repo.getWeather(latitude,longitude,language,units)
                .catch { e ->
                    _weather.value = ApiState.Failure(e)
                }
                .collect { weatherResponse ->
                    _weather.value = ApiState.Success(weatherResponse)
                }
        }
    }
    suspend fun insertCurrentWeatherInHome(weather:WeatherResponse){
        viewModelScope.launch {
            repo.insertCurrentWeather(weather)
        }
    }
    fun getCurrentWeatherInHome() {
        viewModelScope.launch {
            repo.getStoredCurrentWeather()
                .catch {
                    _weather.value = ApiState.Failure(it)
                }
                .collect {
                    _weather.value = ApiState.Success(it[0])
                }
        }
    }

}
