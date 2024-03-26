package com.example.weather_app.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity(tableName = "alerts")
data class AlertModel(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    val Date:String,
    val fromTime:String,
    val toTime:String,
    val alertType:String,
    val longitude:Double,
    val latitude:Double,
    val locationName:String,

):Serializable