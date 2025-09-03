package com.viplearner.weather.data.repository

import com.viplearner.weather.data.dto.CityCoordinateDto
import com.viplearner.weather.data.dto.WeatherResponseDto
import com.viplearner.weather.data.local.dao.UserPreferencesDao
import com.viplearner.weather.data.local.entity.UserPreferencesEntity
import com.viplearner.weather.data.remote.WeatherApiService
import com.viplearner.weather.domain.model.CityCoordinate
import com.viplearner.weather.domain.model.UserPreferences
import com.viplearner.weather.domain.model.Weather
import com.viplearner.weather.domain.repository.WeatherRepository
import com.viplearner.weather.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val userPreferencesDao: UserPreferencesDao
) : WeatherRepository {

    companion object {
        private const val API_KEY = "ed63e3f4bd059ec3cde82c004bee2688"
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather> {
        return try {
            val response = weatherApiService.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = API_KEY
            )
            Result.Success(response.toDomainModel())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchCities(cityName: String): Result<List<CityCoordinate>> {
        return try {
            val response = weatherApiService.getCityCoordinates(
                cityName = cityName,
                apiKey = API_KEY
            )
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserPreferences(): Flow<UserPreferences> {
        return userPreferencesDao.getUserPreferences().map { entity ->
            entity?.toDomainModel() ?: UserPreferences()
        }
    }

    override suspend fun updateFavoriteCity(cityName: String) {
        userPreferencesDao.insertUserPreferences(
            UserPreferencesEntity(favoriteCity = cityName)
        )
    }

    override suspend fun updateTemperatureUnit(unit: String) {
        userPreferencesDao.updateTemperatureUnit(unit)
    }

    private fun WeatherResponseDto.toDomainModel(): Weather {
        return Weather(
            cityName = this.cityName,
            temperature = this.main.temperature,
            description = this.weather.firstOrNull()?.description ?: "",
            humidity = this.main.humidity,
            windSpeed = this.wind.speed,
            feelsLike = this.main.feelsLike,
            pressure = this.main.pressure,
            visibility = this.visibility,
            high = this.main.tempMax,
            low = this.main.tempMin
        )
    }

    private fun CityCoordinateDto.toDomainModel(): CityCoordinate {
        return CityCoordinate(
            name = this.name,
            lat = this.lat,
            lon = this.lon,
            state = this.state,
            country = this.country
        )
    }

    private fun UserPreferencesEntity.toDomainModel(): UserPreferences {
        return UserPreferences(
            favoriteCity = this.favoriteCity,
            temperatureUnit = this.temperatureUnit
        )
    }
}
