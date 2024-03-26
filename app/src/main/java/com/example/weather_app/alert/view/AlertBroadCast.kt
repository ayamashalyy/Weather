package com.example.weather_app.alert.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weather_app.R


class AlertBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val channelId = "weather_channel"
            val builder = NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle("Weather Alert")
                .setContentText("The weather is fine.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(it)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            notificationManager.notify(123, builder.build())
        }
    }
}