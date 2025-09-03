package com.viplearner.weather.domain.model

data class Weather(
    val cityName: String,
    val high: Double,
    val low: Double,
    val temperature: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val feelsLike: Double,
    val pressure: Int,
    val visibility: Int
)
