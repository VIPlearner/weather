package com.viplearner.weather.domain.usecase

import com.viplearner.weather.domain.model.Weather
import com.viplearner.weather.domain.repository.WeatherRepository
import com.viplearner.weather.domain.util.Result
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> {
        return weatherRepository.getCurrentWeather(lat, lon)
    }
}
