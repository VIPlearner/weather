package com.viplearner.weather.ui.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viplearner.weather.domain.model.Weather
import com.viplearner.weather.domain.repository.WeatherRepository
import com.viplearner.weather.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    val weatherState: MutableStateFlow<Weather?> = MutableStateFlow(null)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    fun fetchWeatherForLocation(latitude: Double, longitude: Double) {
        currentLatitude = latitude
        currentLongitude = longitude
        viewModelScope.launch {
            val weather = weatherRepository.getCurrentWeather(latitude, longitude)
            if (weather is Result.Success) {
                weatherState.value = weather.data
            }
        }
    }

    fun refreshWeather() {
        val lat = currentLatitude
        val lon = currentLongitude
        if (lat != null && lon != null) {
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    val weather = weatherRepository.getCurrentWeather(lat, lon)
                    if (weather is Result.Success) {
                        weatherState.value = weather.data
                    }
                } finally {
                    _isRefreshing.value = false
                }
            }
        }
    }
}