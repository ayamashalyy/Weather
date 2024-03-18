package com.example.weather_app.Model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListWeatherConverter {
    @TypeConverter
    fun fromJson(value: String): List<ListWeather> {
        val listType = object : TypeToken<List<ListWeather>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJson(list: List<ListWeather>): String {
        return Gson().toJson(list)
    }

}