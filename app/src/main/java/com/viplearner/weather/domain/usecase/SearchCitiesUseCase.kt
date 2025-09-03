package com.viplearner.weather.domain.usecase

import com.viplearner.weather.domain.model.CityCoordinate
import com.viplearner.weather.domain.repository.WeatherRepository
import com.viplearner.weather.domain.util.Result
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Result<List<CityCoordinate>> {
        return weatherRepository.searchCities(cityName)
    }
}
