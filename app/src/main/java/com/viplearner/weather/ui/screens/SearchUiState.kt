package com.viplearner.weather.ui.screens

import com.viplearner.weather.domain.model.CityCoordinate
import com.viplearner.weather.domain.model.Weather

data class SearchUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchSuggestions: List<CityCoordinate> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val weatherCards: List<WeatherCardData> = emptyList(),
    val isLoadingWeather: Boolean = false,
    val errorMessage: String? = null
)

data class WeatherCardData(
    val location: CityCoordinate,
    val weather: Weather
)
