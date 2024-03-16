package com.example.weather_app.Model

import com.example.weather_app.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class WeatherRepositoryImp private constructor(
    private val remoteSource: WeatherRemoteDataSourceImp) : WeatherRepository {


    companion object {
        private var repo: WeatherRepositoryImp? = null

        fun getInstance(
            remoteSource: WeatherRemoteDataSourceImp,
            // localDataSource: ProductsLocalDataSourceImp
        ): WeatherRepositoryImp {
            if (repo == null) {
                repo = WeatherRepositoryImp(remoteSource)
            }
            return repo!!
        }
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