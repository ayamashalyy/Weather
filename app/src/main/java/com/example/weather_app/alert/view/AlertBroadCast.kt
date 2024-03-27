package com.example.weather_app.alert.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.WeatherRepository
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.R
import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp
import com.example.weather_app.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertBroadCast : BroadcastReceiver() {
    private lateinit var settingsManager: SettingsManager
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            settingsManager = SettingsManager(it)
            val repository = WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                WeatherLocalDataSourceImp.getInstance(context)
            )
            createNotificationChannel(it)
            val alertModel = intent?.getSerializableExtra("alertModel") as AlertModel
            alertModel?.let {
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                coroutineScope.launch {
                    val weather = withContext(Dispatchers.IO) {
                        repository.getWeather(
                            it.latitude,
                            it.longitude,
                            settingsManager.getSelectedTemperatureUnit(),
                            settingsManager.getSelectedLanguage()
                        )
                    }
                    weather.firstOrNull()?.list?.get(0)?.weather?.get(0)
                        ?.let { it1 -> displayNotification(context, it1.description) }
                }

            }
        }
    }
    private fun displayNotification(context: Context, weather: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.header)
            .setContentTitle("Weather Alert")
            .setContentText(weather)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "weather_channel"
        private const val NOTIFICATION_ID = 123

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Weather"
                val descriptionText = "Weather Notifications"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
