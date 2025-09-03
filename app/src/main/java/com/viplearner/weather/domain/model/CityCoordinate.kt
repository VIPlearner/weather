package com.viplearner.weather.domain.model

data class CityCoordinate(
    val name: String,
    val lat: Double,
    val lon: Double,
    val state: String,
    val country: String
)
