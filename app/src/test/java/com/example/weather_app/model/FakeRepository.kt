package com.example.weather_app.model

import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepository
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeRepository : WeatherRepository {
    private val locations: MutableList<FavLocation>? = mutableListOf()
    private val currentWeather : MutableList<WeatherResponse>? = mutableListOf()
    override suspend fun insertLocation(favLocation: FavLocation) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocation(favLocation: FavLocation) {
        TODO("Not yet implemented")
    }

    override fun getStoredLocations(): Flow<List<FavLocation>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeather(weather: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentWeather(weather: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override fun getStoredCurrentWeather(): Flow<List<WeatherResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alertModel: AlertModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alertDetails: AlertModel) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeather(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }
}