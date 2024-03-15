package com.example.weather_app.network

import com.example.weather_app.Model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("exclude") exclude: String?,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") appid: String?
    ): WeatherResponse
}
