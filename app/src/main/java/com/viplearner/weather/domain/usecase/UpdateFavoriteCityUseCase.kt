package com.viplearner.weather.domain.usecase

import com.viplearner.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class UpdateFavoriteCityUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String) {
        weatherRepository.updateFavoriteCity(cityName)
    }
}
