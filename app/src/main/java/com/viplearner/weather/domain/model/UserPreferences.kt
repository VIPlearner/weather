package com.viplearner.weather.domain.model

data class UserPreferences(
    val favoriteCity: String = "",
    val temperatureUnit: String = "metric"
)
