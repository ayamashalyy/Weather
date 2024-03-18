package com.example.weather_app.FavoriteWeather.view

import android.view.View
import androidx.cardview.widget.CardView
import com.example.weather_app.Model.FavLocation

interface OnFavoriteClickListener {
    fun deleteLocations(location: FavLocation)
    fun getWeatherOfFavoriteLocation(favLocation: FavLocation)

}