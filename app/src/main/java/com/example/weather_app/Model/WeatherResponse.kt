package com.example.weather_app.Model

 data class WeatherResponse(
    val list: List<ListWeather>,
    val city: City
)