package com.example.weather_app.FavoriteWeather.view

import com.example.weather_app.Model.FavLocation

interface OnFavoriteClickListener {
    fun deleteLocations(location: FavLocation)
    fun getWeatherOfFavoriteLocation(favLocation: FavLocation)

}