package com.example.weather_app.Model

import kotlinx.coroutines.flow.Flow

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
        units: String,
        lang: String,
    ): Flow<WeatherResponse>
}