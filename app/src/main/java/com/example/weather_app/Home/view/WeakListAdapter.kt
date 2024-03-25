package com.example.weather_app.Home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.Model.ListWeather
import com.example.weather_app.R
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.SettingsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeekListAdapter(private val context: Context) :
    ListAdapter<ListWeather, WeekListAdapter.ViewHolder>(ProductDiffCallback()) {
    private  var settingsManager = SettingsManager(context)
    private val uniqueDays = mutableSetOf<String>()
    private var unit: String = Constants.CELSIUE
    private val filteredList = mutableListOf<ListWeather>()
    fun updateUnit(newUnit: String) {
        unit = newUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.weakly_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = getItem(position)

        Glide.with(context).load("https://openweathermap.org/img/wn/${weather.weather[0].icon}.png")
            .into(holder.img)

        val date = Date(weather.dt * 1000L)
        val dayOfWeek = SimpleDateFormat("EEEE", Locale(settingsManager.getSelectedLanguage())).format(date)
        holder.day.text = dayOfWeek

        val temperatureInCelsius = weather.main.temp.toInt()
        val temperatureString = when (unit) {
            "metric" -> "${temperatureInCelsius}\u2103"
            "standard" -> "${temperatureInCelsius}\u212A"
            "imperial" -> "${temperatureInCelsius}\u2109"
            else -> "${temperatureInCelsius}\u2103"
        }
        holder.temp.text = temperatureString
        holder.status.text = weather.weather[0].description
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.weaklyWeatherImg)
        val day: TextView = itemView.findViewById(R.id.weakly_day_txt)
        val temp: TextView = itemView.findViewById(R.id.weakly_temp_txt)
        val status: TextView = itemView.findViewById(R.id.weekly_weather_status_txt)
    }

    override fun submitList(list: List<ListWeather>?) {
        list?.let {
            filteredList.clear()
            uniqueDays.clear()
            it.forEach { weather ->
                val date = Date(weather.dt * 1000L)
                val dayOfWeek = SimpleDateFormat("EEEE", Locale(settingsManager.getSelectedLanguage())).format(date)
                if (uniqueDays.add(dayOfWeek)) {
                    filteredList.add(weather)
                    if (uniqueDays.size >= 5) return@forEach
                }
            }
            super.submitList(filteredList)
        }
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
