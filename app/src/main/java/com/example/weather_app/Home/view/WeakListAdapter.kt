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
import java.text.SimpleDateFormat
import java.util.Locale

class WeekListAdapter(private val context: Context) :
    ListAdapter<ListWeather, WeekListAdapter.ViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.weakly_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = getItem(position)

        Glide.with(context).load("https://openweathermap.org/img/wn/${weather.weather[0].icon}.png")
            .into(holder.img)
        holder.day.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(weather.dt_txt))
        holder.temp.text = "${weather.main.temp}°C"
        holder.status.text = weather.weather[0].description
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.weaklyWeatherImg)
        val day: TextView = itemView.findViewById(R.id.weakly_day_txt)
        val temp: TextView = itemView.findViewById(R.id.weakly_temp_txt)
        val status:TextView = itemView.findViewById(R.id.weekly_weather_status_txt)
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
