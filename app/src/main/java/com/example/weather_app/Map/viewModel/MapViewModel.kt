package com.example.weather_app.Map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(private val _repo: WeatherRepository) : ViewModel() {
    fun addLocationToFav(favLocation: FavLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertLocation(favLocation)
        }


    }
}