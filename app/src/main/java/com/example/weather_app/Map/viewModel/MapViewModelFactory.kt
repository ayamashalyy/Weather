package com.example.weather_app.Map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.Model.WeatherRepository

class MapViewModelFactory (private val _repo: WeatherRepository)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(_repo) as T
        }else
            throw IllegalArgumentException("class not found")
    }
}