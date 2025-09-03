package com.viplearner.weather.domain.usecase

import com.viplearner.weather.domain.model.UserPreferences
import com.viplearner.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(): Flow<UserPreferences> {
        return weatherRepository.getUserPreferences()
    }
}
