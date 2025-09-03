package com.viplearner.weather.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("name")
    val cityName: String,
    @SerialName("main")
    val main: MainDto,
    @SerialName("weather")
    val weather: List<WeatherInfoDto>,
    @SerialName("wind")
    val wind: WindDto,
    @SerialName("visibility")
    val visibility: Int
)


@Serializable
data class MainDto(
    @SerialName("temp")
    val temperature: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("sea_level")
    val seaLevel: Int,
    @SerialName("grnd_level")
    val groundLevel: Int
)
@Serializable
data class WeatherInfoDto(
    @SerialName("main")
    val main: String,
    @SerialName("description")
    val description: String
)

@Serializable
data class WindDto(
    @SerialName("speed")
    val speed: Double
)
