package com.example.weather_app.network

import com.example.weather_app.Model.WeatherResponse
import com.example.weather_app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") appid: String = Constants.API_KEY
    ): WeatherResponse
}
