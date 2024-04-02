package com.example.weather_app.FavoriteWeather.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.ApiState
import com.example.weather_app.LoadingState
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavWeatherViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _weather: MutableStateFlow<LoadingState> = MutableStateFlow(LoadingState.Loading)
    val weather: StateFlow<LoadingState> get() = _weather

    init {
        getLocalLocations()
    }

    fun deleteLocations(location: FavLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLocation(location)
            getLocalLocations()
        }
    }

    private fun getLocalLocations() {
        viewModelScope.launch {
            repo.getStoredLocations().catch { e ->
                _weather.value = LoadingState.Error(e.message ?: "An error occurred")
            }.collect { weatherResponse ->
                _weather.value = LoadingState.Success(weatherResponse)
            }
        }
    }
}
