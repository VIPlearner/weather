package com.viplearner.weather.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CityCoordinateDto(
    val name: String,
    val localNames: Map<String, String> = emptyMap(),
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String = ""
)
