package com.example.weather_app.dp

import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocal(private val locations: MutableList<FavLocation>? = mutableListOf() , private val currentWeather : MutableList<WeatherResponse>? = mutableListOf()) :
    localDataSourse {
    override fun getAllStoredLocations(): Flow<List<FavLocation>> {
        return flow {
            val location = locations?.toList()
            if (location.isNullOrEmpty()) {
                emit(emptyList())
            } else {
                emit(location)
            }
        }
    }



    override fun deleteLocation(location: FavLocation) {
        locations?.remove(location)
    }

    override fun insertLocation(location: FavLocation) {
        locations?.add(location)
    }

    override fun getAllStoredCurrentWeather(): Flow<List<WeatherResponse>> {
        return flow {
            val weather = currentWeather?.toList()
            if (weather.isNullOrEmpty()) {
                emit(emptyList())
            } else {
                emit(weather)
            }
        }
    }

    override fun deleteCurrentWeather(weather: WeatherResponse) {
        currentWeather?.remove(weather)
    }

    override fun insertCurrentWeather(weather: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override fun insertAlert(alert: AlertModel) {
        TODO("Not yet implemented")
    }

    override fun getAllAlerts(): Flow<List<AlertModel>> {
        TODO("Not yet implemented")
    }

    override fun deleteAlert(alert: AlertModel) {
        TODO("Not yet implemented")
    }
}