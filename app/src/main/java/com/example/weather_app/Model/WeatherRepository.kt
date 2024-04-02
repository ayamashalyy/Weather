package com.example.weather_app.Model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun insertLocation(favLocation: FavLocation)
    suspend fun deleteLocation(favLocation: FavLocation)
    fun getStoredLocations(): Flow<List<FavLocation>>
    suspend fun insertCurrentWeather(weather: WeatherResponse)
    suspend fun deleteAllCurrentWeather()
    fun getStoredCurrentWeather(): Flow<List<WeatherResponse>>
    suspend fun insertAlert(alertModel: AlertModel)
    suspend fun deleteAlert(alertDetails: AlertModel)
    suspend fun getAllAlerts(): Flow<List<AlertModel>>
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
    ): Flow<WeatherResponse>
}