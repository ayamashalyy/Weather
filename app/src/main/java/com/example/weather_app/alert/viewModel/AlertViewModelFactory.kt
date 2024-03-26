package com.example.weather_app.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.Model.WeatherRepository

class AlertViewModelFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(repo) as T

        } else {
            throw IllegalArgumentException("viewModel Class Not Found")
        }
    }
}