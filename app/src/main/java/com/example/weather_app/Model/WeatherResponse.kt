package com.example.weather_app.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentWeather")
data class WeatherResponse(
    val list: List<ListWeather>,
    @PrimaryKey
    val city: City,
)
