package com.example.weather_app.model

import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepository
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : WeatherRepository {
    private val locations: MutableList<FavLocation>? = mutableListOf()
    private val currentWeather : MutableList<WeatherResponse>? = mutableListOf()
    private val alerts : MutableList<AlertModel>? = mutableListOf()

    override suspend fun insertLocation(favLocation: FavLocation) {
        locations?.add(favLocation)
    }

    override suspend fun deleteLocation(favLocation: FavLocation) {
        locations?.remove(favLocation)
    }

    override fun getStoredLocations(): Flow<List<FavLocation>> {
        return flow {
            val location = locations?.toList()
            if (location.isNullOrEmpty()) {
                emit(emptyList())
            } else {
                emit(location)
            }
        }
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

    override suspend fun insertAlert(alert: AlertModel) {
        alerts?.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertModel) {
        alerts?.remove(alert)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        return flow {
            val alert = alerts?.toList()
            if (alert.isNullOrEmpty()) {
                emit(emptyList())
            } else {
                emit(alert)
            }
        }
    }

    override suspend fun getWeather(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }
}