package com.example.weather_app.alert.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.R
import com.example.weather_app.alert.viewModel.AlertViewModel
import java.util.*
import kotlin.math.abs

class AlertDialog(val viewModel: AlertViewModel) : DialogFragment(),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var selectedDateTextView: TextView
    private lateinit var fromTimeTextView: TextView
    private lateinit var toTimeTextView: TextView
    private lateinit var save: Button
    private lateinit var selectedTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var notification: RadioButton
    private lateinit var alarm: RadioButton
    private lateinit var type: String
    private lateinit var selectedDateCalendar: Calendar
    private lateinit var selectedStartTimeCalendar: Calendar
    private lateinit var selectedendTimeCalendar: Calendar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_alart, container, false)
        val sharedPreferences =
            requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        selectedDateTextView = view.findViewById(R.id.alert_date_txt)
        fromTimeTextView = view.findViewById(R.id.alert_from_txt)
        toTimeTextView = view.findViewById(R.id.alert_to_txt)
        save = view.findViewById(R.id.save_alert_btn)
        radioGroup = view.findViewById(R.id.alert_radio_group)
        notification = view.findViewById(R.id.radio_notification)
        alarm = view.findViewById(R.id.radio_alarm)
        selectedDateCalendar = Calendar.getInstance()
        selectedStartTimeCalendar = Calendar.getInstance()
        selectedendTimeCalendar = Calendar.getInstance()


        val calendarButton = view.findViewById<ImageView>(R.id.calender_btn)
        val fromTimeButton = view.findViewById<ImageView>(R.id.from_time_btn)
        val toTimeButton = view.findViewById<ImageView>(R.id.to_time_btn)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val alertType = view.findViewById<View>(checkedId) as RadioButton
            type = if (alertType == notification) {
                "notification"
            } else {
                "alarm"
            }
        }
        calendarButton.setOnClickListener {
            showDatePicker()
        }
        fromTimeButton.setOnClickListener {
            showTimePicker(fromTimeTextView)
        }
        toTimeButton.setOnClickListener {
            showTimePicker(toTimeTextView)
        }
        save.setOnClickListener {
            if (selectedDateTextView.text.isEmpty() || fromTimeTextView.text.isEmpty() || toTimeTextView.text.isEmpty() || (!notification.isChecked && !alarm.isChecked)) {
                Toast.makeText(
                    requireContext(), "Required Empty Fields", Toast.LENGTH_SHORT
                ).show()
            } else {
                val alert = AlertModel(
                    Date = selectedDateTextView.text.toString(),
                    fromTime = fromTimeTextView.text.toString(),
                    toTime = toTimeTextView.text.toString(),
                    alertType = type,
                    latitude = sharedPreferences.getString("latitude", "0.0")!!.toDouble(),
                    longitude = sharedPreferences.getString("longitude", "0.0")!!.toDouble(),
                    locationName = sharedPreferences.getString("locationName", "UnKnownPlace")!!
                )
                viewModel.addAlert(alert)
                Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), AlertBroadCast::class.java)
                intent.putExtra("alertModel", alert)
                intent.putExtra("type", alert.alertType)

                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                    requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager =
                    requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val start = Calendar.getInstance()
                start.set(
                    selectedDateCalendar.get(Calendar.YEAR),
                    selectedDateCalendar.get(Calendar.MONTH),
                    selectedDateCalendar.get(Calendar.DAY_OF_MONTH),
                    selectedStartTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedStartTimeCalendar.get(Calendar.MINUTE),
                    selectedStartTimeCalendar.get(Calendar.SECOND)
                )
                Log.i("TAG", "${start} ")
                var firstAlarm = abs(start.timeInMillis)
                Log.i("TAG", "${firstAlarm} ")
                Log.i("TAG", "${Calendar.getInstance().timeInMillis} ")


                val end = Calendar.getInstance()
                end.set(
                    selectedDateCalendar.get(Calendar.YEAR),
                    selectedDateCalendar.get(Calendar.MONTH),
                    selectedDateCalendar.get(Calendar.DAY_OF_MONTH),
                    selectedendTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedendTimeCalendar.get(Calendar.MINUTE),
                    selectedendTimeCalendar.get(Calendar.SECOND)

                )

                val selectedDateTimeInMillis = abs(start.timeInMillis - end.timeInMillis)
                Log.i("TAG", "${selectedDateTimeInMillis} ")
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP, firstAlarm , pendingIntent
                )
                dialog?.dismiss()
            }
        }

        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_DeviceDefault_Light_Dialog,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDateCalendar.set(Calendar.YEAR, year)
        selectedDateCalendar.set(Calendar.MONTH, month)
        selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        Log.i("TAG", "Year set to: ${currentDate.get(Calendar.YEAR)}")
        Log.i("TAG", "Month set to: ${currentDate.get(Calendar.MONTH)}")
        Log.i("TAG", "Day of month set to: ${currentDate.get(Calendar.DAY_OF_MONTH)}")


        if (selectedDateCalendar.before(currentDate)) {
            Toast.makeText(
                requireContext(), "Please select a valid date", Toast.LENGTH_SHORT
            ).show()
        } else {
            selectedDateTextView.text = "$dayOfMonth/$month/$year"
        }
    }


    private fun showTimePicker(textView: TextView) {
        selectedTextView = textView
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
            this,
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (selectedTextView === fromTimeTextView) {
            fromTimeTextView.text = String.format("%02d:%02d", hourOfDay, minute)
            selectedStartTimeCalendar.apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                Log.i("TAG", "${selectedStartTimeCalendar.timeInMillis} ")
            }
        } else {
            toTimeTextView.text = String.format("%02d:%02d", hourOfDay, minute)
            selectedendTimeCalendar.apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
        }
    }
}
