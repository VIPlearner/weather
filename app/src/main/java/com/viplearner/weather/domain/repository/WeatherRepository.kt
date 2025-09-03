package com.viplearner.weather.domain.repository

import com.viplearner.weather.domain.model.CityCoordinate
import com.viplearner.weather.domain.model.UserPreferences
import com.viplearner.weather.domain.model.Weather
import com.viplearner.weather.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather>
    suspend fun searchCities(cityName: String): Result<List<CityCoordinate>>
    suspend fun getUserPreferences(): Flow<UserPreferences>
    suspend fun updateFavoriteCity(cityName: String)
    suspend fun updateTemperatureUnit(unit: String)
}
