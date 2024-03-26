package com.example.weather_app.dp

import android.content.Context
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WeatherLocalDataSourceImp private constructor(context: Context) {
    private val weatherDAO: WeatherDao by lazy {
        val db: AppDataBase = AppDataBase.getInstance(context)
        db.getWeatherDao()
    }

    companion object {
        private var localSource: WeatherLocalDataSourceImp? = null

        fun getInstance(context: Context): WeatherLocalDataSourceImp {
            if (localSource == null) {
                localSource = WeatherLocalDataSourceImp(context)
            }
            return localSource!!
        }
    }

    fun getAllStoredLocations(): Flow<List<FavLocation>> {
        return weatherDAO.getAllLocations()
    }

    fun deleteLocation(location: FavLocation) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.deleteLocation(location)
        }
    }

    fun insertLocation(location: FavLocation) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.insertLocation(location)
        }
    }

    fun getAllStoredCurrentWeather(): Flow<List<WeatherResponse>> {
        return weatherDAO.getAllCurrentWeather()
    }

    fun deleteCurrentWeather(weather: WeatherResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.deleteCurrentWeather(weather)
        }
    }

    fun insertCurrentWeather(weather: WeatherResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.insertCurrentWeather(weather)
        }
    }

    fun insertAlert(alert: AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.insertAlert(alert)
        }
    }

    fun getAllAlerts(): Flow<List<AlertModel>> {
        return weatherDAO.allAlerts()
    }

    fun deleteAlert(alert: AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.deleteAlert(alert)
        }
    }

}