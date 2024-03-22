package com.example.weather_app.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather_app.Model.CityConverter
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.ListWeatherConverter
import com.example.weather_app.Model.WeatherResponse

@Database(entities = arrayOf(FavLocation::class, WeatherResponse::class), version = 1)
@TypeConverters(ListWeatherConverter::class, CityConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getInstance(ctx: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, AppDataBase::class.java, "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}