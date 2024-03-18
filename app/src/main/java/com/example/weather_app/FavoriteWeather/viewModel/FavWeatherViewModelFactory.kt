package com.example.weather_app.FavoriteWeather.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.Model.WeatherRepository

class FavWeatherViewModelFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavWeatherViewModel::class.java)) {
            FavWeatherViewModel(repo) as T

        }
        else{
            throw IllegalArgumentException("viewModel Class Not Found")
        }
    }
}