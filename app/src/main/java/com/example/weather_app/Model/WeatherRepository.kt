package com.example.weather_app.Model

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        exclude: String?="minutely",
        units: String?="metric",
        lang:String?="en",
        appid: String?="111c965ece2127ae635f772022192953"
    ): WeatherResponse
}