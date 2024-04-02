package com.example.weather_app.dp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM favLocation")
    fun getAllLocations(): Flow<List<FavLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FavLocation)

    @Delete
    suspend fun deleteLocation(location: FavLocation)

    @Query("SELECT * FROM currentWeather")
    fun getAllCurrentWeather(): Flow<List<WeatherResponse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: WeatherResponse)

    @Delete
    suspend fun deleteCurrentWeather(weather: WeatherResponse)
    @Query("DELETE FROM currentWeather")
    suspend fun deleteAllCurrentWeather()

    @Query("SELECT * FROM alerts")
    fun allAlerts(): Flow<List<AlertModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertModel)

    @Delete
    suspend fun deleteAlert(alert: AlertModel)
}