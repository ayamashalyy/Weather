package com.example.weather_app.alert.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.R

class AlertListAdapter (private val context: Context, private val listener: OnClickListener) :
  ListAdapter<AlertModel, AlertListAdapter.ViewHolder>(ProductDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fav_alert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alert = getItem(position)
        holder.title.text = alert.Date
        holder.fromTime.text = alert.fromTime
        holder.toTime.text = alert.toTime
        holder.delete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure you want to delete this Place?")
                .setPositiveButton("OK") { dialog, which ->
                    listener.removeFromAlert(alert)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }.setCancelable(false).show()

        }


    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.place_txt)
        val delete: ImageView = itemView.findViewById(R.id.delete_alert_btn)
        val fromTime: TextView = itemView.findViewById(R.id.from_txt)
        val toTime:TextView = itemView.findViewById(R.id.to_txt)

    }
    class ProductDiffCallback : DiffUtil.ItemCallback<AlertModel>() {
        override fun areItemsTheSame(oldItem: AlertModel, newItem: AlertModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlertModel, newItem: AlertModel): Boolean {
            return oldItem == newItem
        }
    }

}