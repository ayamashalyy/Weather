package com.example.weather_app.Model

import com.example.weather_app.dp.localDataSourse
import com.example.weather_app.network.remoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRepositoryImp private constructor(
    private val remoteSource: remoteDataSource,
    private val localDataSource: localDataSourse
) : WeatherRepository {


    companion object {
        private var repo: WeatherRepositoryImp? = null

        fun getInstance(
            remote: remoteDataSource, local: localDataSourse
        ): WeatherRepositoryImp {
            if (repo == null) {
                repo = WeatherRepositoryImp(remote, local)
            }
            return repo!!
        }
    }

    override suspend fun insertLocation(location: FavLocation) {
        localDataSource.insertLocation(location)

    }

    override suspend fun deleteLocation(location: FavLocation) {
        localDataSource.deleteLocation(location)
    }

    override fun getStoredLocations(): Flow<List<FavLocation>> {
        return localDataSource.getAllStoredLocations()

    }

    override suspend fun insertCurrentWeather(weather: WeatherResponse) {
        localDataSource.insertCurrentWeather(weather)
    }


    override suspend fun deleteAllCurrentWeather() {
        localDataSource.deleteAllCurrentWeather()
    }

    override fun getStoredCurrentWeather(): Flow<List<WeatherResponse>> {
        return localDataSource.getAllStoredCurrentWeather()
    }

    override suspend fun insertAlert(alertModel: AlertModel) {
        localDataSource.insertAlert(alertModel)
    }

    override suspend fun deleteAlert(alertDetails: AlertModel) {
        localDataSource.deleteAlert(alertDetails)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        return localDataSource.getAllAlerts()
    }


    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
    ): Flow<WeatherResponse> {
        return flowOf(remoteSource.makeNetworkCall(lat, lon, units, lang))
    }

}