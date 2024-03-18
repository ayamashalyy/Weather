package com.example.weather_app.Model

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {
    suspend fun insertLocation(favLocation: FavLocation)
    suspend fun deleteLocation(favLocation: FavLocation)
    fun getStoredLocations(): Flow<List<FavLocation>>
    suspend fun insertCurrentWeather(weather: WeatherResponse)
    suspend fun deleteCurrentWeather(weather: WeatherResponse)
    fun getStoredCurrentWeather(): Flow<List<WeatherResponse>>
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        exclude: String?="minutely",
        units: String?="metric",
        lang:String?="en",
        appid: String?="111c965ece2127ae635f772022192953"
    ): Flow<WeatherResponse>
}