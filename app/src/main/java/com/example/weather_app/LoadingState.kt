package com.example.weather_app

import com.example.weather_app.Model.FavLocation

sealed class LoadingState {
    object Loading : LoadingState()
    data class Success(val data: List<FavLocation>) : LoadingState()
    data class Error(val message: String) : LoadingState()
}
