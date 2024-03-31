package com.example.weather_app.FavoriteWeather.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavWeatherViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _weather: MutableLiveData<List<FavLocation>> = MutableLiveData()
    val weather: LiveData<List<FavLocation>> get() = _weather

    init {
        getLocalLocations()
    }

    fun deleteLocations(location: FavLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLocation(location)
            getLocalLocations()
        }
    }

     fun getLocalLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getStoredLocations().collect {
                _weather.postValue(it)
            }
        }
    }

}