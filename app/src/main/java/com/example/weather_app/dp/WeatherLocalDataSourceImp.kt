package com.example.weather_app.dp

import android.content.Context
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WeatherLocalDataSourceImp (context: Context):localDataSourse {
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

    override fun getAllStoredLocations(): Flow<List<FavLocation>> {
        return weatherDAO.getAllLocations()
    }

    override fun deleteLocation(location: FavLocation) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.deleteLocation(location)
        }
    }

    override fun insertLocation(location: FavLocation) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.insertLocation(location)
        }
    }

    override fun getAllStoredCurrentWeather(): Flow<List<WeatherResponse>> {
        return weatherDAO.getAllCurrentWeather()
    }

    override fun deleteCurrentWeather(weather: WeatherResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.deleteCurrentWeather(weather)
        }
    }

    override fun insertCurrentWeather(weather: WeatherResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.insertCurrentWeather(weather)
        }
    }

    override fun insertAlert(alert: AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.insertAlert(alert)
        }
    }

    override fun getAllAlerts(): Flow<List<AlertModel>> {
        return weatherDAO.allAlerts()
    }

    override fun deleteAlert(alert: AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDAO.deleteAlert(alert)
        }
    }

}