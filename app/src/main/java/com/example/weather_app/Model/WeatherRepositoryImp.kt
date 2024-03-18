package com.example.weather_app.Model

import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class WeatherRepositoryImp private constructor(
    private val remoteSource: WeatherRemoteDataSourceImp,
    private val localDataSource: WeatherLocalDataSourceImp
    ) : WeatherRepository {


    companion object {
        private var repo: WeatherRepositoryImp? = null

        fun getInstance(
            remoteSource: WeatherRemoteDataSourceImp,
            localDataSource: WeatherLocalDataSourceImp
        ): WeatherRepositoryImp {
            if (repo == null) {
                repo = WeatherRepositoryImp(remoteSource,localDataSource)
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

    override suspend fun deleteCurrentWeather(weather: WeatherResponse) {
        localDataSource.deleteCurrentWeather(weather)
    }

    override fun getStoredCurrentWeather(): Flow<List<WeatherResponse>>{
       return localDataSource.getAllStoredCurrentWeather()
    }


    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        exclude: String?,
        units: String?,
        lang: String?,
        appid: String?
    ): Flow<WeatherResponse> {
      return flowOf(remoteSource.makeNetworkCall(lat, lon, units, lang, appid))
    }

}