package com.example.weather_app.Model

import androidx.room.TypeConverter
import com.google.gson.Gson

class CityConverter {
        @TypeConverter
        fun fromJson(value: String): City {
            return Gson().fromJson(value, City::class.java)
        }

        @TypeConverter
        fun toJson(city: City): String {
            return Gson().toJson(city)
        }
    }

