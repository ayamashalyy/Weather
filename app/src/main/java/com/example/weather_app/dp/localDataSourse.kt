package com.example.weather_app.dp

import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface localDataSourse {
    fun getAllStoredLocations(): Flow<List<FavLocation>>
    fun deleteLocation(location: FavLocation)
    fun insertLocation(location: FavLocation)
    fun getAllStoredCurrentWeather(): Flow<List<WeatherResponse>>
    fun deleteAllCurrentWeather()
    fun insertCurrentWeather(weather: WeatherResponse)
    fun insertAlert(alert: AlertModel)
    fun getAllAlerts(): Flow<List<AlertModel>>
    fun deleteAlert(alert: AlertModel)


}