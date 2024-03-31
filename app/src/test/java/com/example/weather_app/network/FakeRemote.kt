package com.example.weather_app.network

import com.example.weather_app.Model.WeatherResponse


class FakeRemote (private val response: WeatherResponse) : remoteDataSource {

    override suspend fun makeNetworkCall(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse {
        return response
    }
}