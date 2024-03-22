package com.example.weather_app.network

import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRemoteDataSourceImp private constructor() {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private var weatherService: WeatherService

    init {
        val retrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
                .build()
        weatherService = retrofit.create(WeatherService::class.java)
    }

    companion object {
        private var client: WeatherRemoteDataSourceImp? = null

        fun getInstance(): WeatherRemoteDataSourceImp {
            if (client == null) {
                client = WeatherRemoteDataSourceImp()
            }
            return client as WeatherRemoteDataSourceImp
        }
    }


    suspend fun makeNetworkCall(
        lat: Double, lon: Double, units: String, lang: String
    ): WeatherResponse {
        return withContext(Dispatchers.IO) {
            weatherService.getWeather(lat, lon, units, lang)
        }
    }
}