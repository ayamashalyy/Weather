package com.example.weather_app.Home.viewModel

import androidx.lifecycle.ViewModel
import com.example.weather_app.Model.WeatherRepository
import com.example.weather_app.Model.WeatherResponse
import androidx.lifecycle.viewModelScope
import com.example.weather_app.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepository) : ViewModel() {
    val _weather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val dataBase:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    suspend fun getMyWeatherStatus(
        latitude: Double, longitude: Double, units: String, language: String
    ) {
        viewModelScope.launch {
            repo.getWeather(latitude, longitude, units, language).catch { e ->
                _weather.value = ApiState.Failure(e)
            }.collect { weatherResponse ->
                _weather.value = ApiState.Success(weatherResponse)
            }
        }
    }

    suspend fun insertCurrentWeatherInHome(weather: WeatherResponse) {
        viewModelScope.launch {
            repo.insertCurrentWeather(weather)
        }
    }

    suspend fun deleteAllCurrentWeatherInHome() {
        viewModelScope.launch {
            repo.deleteAllCurrentWeather()
        }
    }

    fun getCurrentWeatherInHome() {
        viewModelScope.launch {
            repo.getStoredCurrentWeather().catch {
                    dataBase.value = ApiState.Failure(it)
                }.collect {
                    dataBase.value = ApiState.Success(it[0])
                }

        }

    }
}
