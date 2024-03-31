package com.example.weather_app.alert.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertBroadCast : BroadcastReceiver() {
    private lateinit var settingsManager: SettingsManager
    private lateinit var windowManager: WindowManager
    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        settingsManager = SettingsManager(context)
        val repository = WeatherRepositoryImp.getInstance(
            WeatherRemoteDataSourceImp.getInstance(), WeatherLocalDataSourceImp.getInstance(context)
        )
        val alertModel = intent?.getSerializableExtra("alertModel") as AlertModel
        val type = intent?.getStringExtra("type")
        if (type == "notification") {
            createNotificationChannel(context)
            getNetWorkCall(repository, context, intent, alertModel)
            deleteAlertItem(repository, alertModel)
        } else if (type == "alarm") {
            alerm(repository, context, intent, alertModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAlarm(context: Context, des: String) {
        try {
            view = LayoutInflater.from(context).inflate(R.layout.response_alert, null, false)
            val description = view.findViewById<TextView>(R.id.alert_txt)
            val dismiss = view.findViewById<Button>(R.id.dismiss_btn)
            val layout = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            layout.gravity = Gravity.TOP
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.addView(view, layout)
            description.text = des
            dismiss.setOnClickListener {
                cancel()
            }

        } catch (e: WindowManager.BadTokenException) {
            Toast.makeText(
                context, "${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun cancel() {
        windowManager.removeView(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun alerm(
        repository: WeatherRepository, context: Context, intent: Intent?, alertModel: AlertModel
    ) {
        alertModel.let {
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
                val des = weather.firstOrNull()?.list?.get(0)?.weather?.get(0)?.description
                showAlarm(context,des.toString())


            }

        }
    }

    private fun getNetWorkCall(
        repository: WeatherRepositoryImp, context: Context, intent: Intent?, alertModel: AlertModel
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val weather = withContext(Dispatchers.IO) {
                repository.getWeather(
                    alertModel.latitude,
                    alertModel.longitude,
                    settingsManager.getSelectedTemperatureUnit(),
                    settingsManager.getSelectedLanguage()
                )
            }
            weather.collectLatest {
                displayNotification(context, it.list[0].weather[0].description)
            }
        }

    }

    private fun displayNotification(context: Context, weather: String) {
        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.card_shape)
                .setContentTitle("Weather Alert").setContentText(weather).setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.header))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_NOTIFICATION_POLICY
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

    private fun deleteAlertItem(repository: WeatherRepositoryImp, alertModel: AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.deleteAlert(alertModel)
            } catch (e: Exception) {
                Log.e("DeleteAlertService", "Error deleting item: ${e.message}")
            }
        }
    }

}
