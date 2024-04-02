package com.example.weather_app.FavoriteWeather.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.R

class favListAdapter(private val context: Context, private val listener: OnFavoriteClickListener) :
    ListAdapter<FavLocation, favListAdapter.ViewHolder>(ProductDiffCallback()) {

    fun setList(updateProducts: MutableList<FavLocation>) {
        submitList(updateProducts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fav_map_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = getItem(position)

        val addressParts = weather.address?.split(",")
        val firstLineOfAddress = addressParts?.firstOrNull() ?: ""
        holder.title.text = firstLineOfAddress
        holder.delete.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure you want to delete this Place?")
                .setPositiveButton("OK") { dialog, which ->
                    listener.deleteLocations(weather)
                }

                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }.setCancelable(false).show()

        }
        holder.card.setOnClickListener {
            listener.getWeatherOfFavoriteLocation(weather)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.fav_place_txt)
        val delete: ImageView = itemView.findViewById(R.id.delete_btn)
        val card: CardView = itemView.findViewById(R.id.fav_map_place_card_view)

    }

    class ProductDiffCallback : DiffUtil.ItemCallback<FavLocation>() {
        override fun areItemsTheSame(oldItem: FavLocation, newItem: FavLocation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavLocation, newItem: FavLocation): Boolean {
            return oldItem == newItem
        }
    }
}
