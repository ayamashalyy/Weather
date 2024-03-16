package com.example.weather_app

import com.example.weather_app.Model.WeatherResponse

sealed class ApiState {
    data class Success(val data: WeatherResponse) : ApiState()
    data class Failure(val msg : Throwable) : ApiState()
    data object Loading :ApiState()

}