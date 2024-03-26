package com.example.weather_app.alert.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertViewModel(private val repo: WeatherRepository) : ViewModel() {
    private var _alert: MutableLiveData<List<AlertModel>> = MutableLiveData()
    val alert: LiveData<List<AlertModel>> get() = _alert

    init {
        getAlerts()
    }

    private fun getAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlerts().collect {
                _alert.postValue(it)
            }
        }
    }
    fun removeAlert(alert: AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlert(alert)
            getAlerts()
        }
    }
    fun addAlert(alert: AlertModel){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlert(alert)
            getAlerts()
        }
    }

}