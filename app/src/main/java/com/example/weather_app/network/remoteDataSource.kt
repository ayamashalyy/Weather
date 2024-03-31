package com.example.weather_app.network

import com.example.weather_app.Model.WeatherResponse

interface remoteDataSource {
    suspend fun makeNetworkCall(lat: Double, lon: Double, units: String, lang: String): WeatherResponse
}