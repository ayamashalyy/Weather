package com.example.weather_app.Home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.Model.ListWeather
import com.example.weather_app.R
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.SettingsManager
import java.text.SimpleDateFormat
import java.util.Locale

class DayListAdapter(private val context: Context) :
    ListAdapter<ListWeather, DayListAdapter.ViewHolder>(ProductDiffCallback()) {
    private var settingsManager = SettingsManager(context)
    private var unit: String = Constants.CELSIUE

    fun updateUnit(newUnit: String) {
        unit = newUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.hourly_wather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = getItem(position)

        Glide.with(context).load("https://openweathermap.org/img/wn/${weather.weather[0].icon}.png")
            .into(holder.img)
        holder.hour.text = SimpleDateFormat(
            "hh:mm a", Locale(settingsManager.getSelectedLanguage())
        ).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale(settingsManager.getSelectedLanguage())).parse(weather.dt_txt))

        val temperatureInCelsius = weather.main.temp.toInt()
        val temperatureString = when (unit) {
            "metric" -> "${temperatureInCelsius}\u2103"
            "standard" -> "${temperatureInCelsius}\u212A"
            "imperial" -> "${temperatureInCelsius}\u2109"
            else -> "${temperatureInCelsius}\u2103"
        }
        holder.temp.text = temperatureString
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.hourlyWeatherImg)
        val hour: TextView = itemView.findViewById(R.id.hourly_day_txt)
        val temp: TextView = itemView.findViewById(R.id.hourly_temp_txt)
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ListWeather>() {
        override fun areItemsTheSame(oldItem: ListWeather, newItem: ListWeather): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ListWeather, newItem: ListWeather): Boolean {
            return oldItem == newItem
        }
    }
}
