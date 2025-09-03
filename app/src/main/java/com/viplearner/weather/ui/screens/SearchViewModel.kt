package com.viplearner.weather.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viplearner.weather.domain.model.CityCoordinate
import com.viplearner.weather.domain.usecase.GetCurrentWeatherUseCase
import com.viplearner.weather.domain.usecase.SearchCitiesUseCase
import com.viplearner.weather.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.isNotBlank() && it.length >= 2 }
            .onEach { query ->
                searchCities(query)
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            searchSuggestions = if (query.isEmpty()) emptyList() else _uiState.value.searchSuggestions
        )
    }

    fun setSearchMode(isSearching: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSearching = isSearching,
            searchSuggestions = if (!isSearching) emptyList() else _uiState.value.searchSuggestions
        )
    }

    fun selectLocation(location: CityCoordinate) {
        _uiState.value = _uiState.value.copy(
            isSearching = false,
            searchQuery = "",
            searchSuggestions = emptyList()
        )
        fetchWeatherForSingleLocation(location)
    }

    fun searchAllResults() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isBlank() || currentQuery.length < 2) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingWeather = true,
                isSearching = false,
                searchSuggestions = emptyList()
            )

            when (val searchResult = searchCitiesUseCase(currentQuery)) {
                is Result.Success -> {
                    val locations = searchResult.data
                    val weatherResults = mutableListOf<WeatherCardData>()

                    locations.forEach { location ->
                        when (val weatherResult = getCurrentWeatherUseCase(location.lat, location.lon)) {
                            is Result.Success -> {
                                weatherResults.add(WeatherCardData(location, weatherResult.data))
                            }
                            is Result.Error -> {
                            }
                            else -> {
                            }
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        weatherCards = weatherResults,
                        isLoadingWeather = false,
                        searchQuery = ""
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        errorMessage = "Unable to load search results"
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        errorMessage = "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun searchCities(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSuggestions = true)

            when (val result = searchCitiesUseCase(query)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        searchSuggestions = result.data,
                        isLoadingSuggestions = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingSuggestions = false,
                        errorMessage = "Unable to load search results"
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingSuggestions = false,
                        errorMessage = "Unknown error occurred"
                    )
                }
            }
        }
    }

    private fun fetchWeatherForSingleLocation(location: CityCoordinate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWeather = true)

            when (val result = getCurrentWeatherUseCase(location.lat, location.lon)) {
                is Result.Success -> {
                    val newWeatherCard = WeatherCardData(location, result.data)

                    _uiState.value = _uiState.value.copy(
                        weatherCards = listOf(newWeatherCard),
                        isLoadingWeather = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        errorMessage = "Unable to load weather data"
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        errorMessage = "Unknown error occurred"
                    )
                }
            }
        }
    }

    private fun fetchWeatherForLocation(location: CityCoordinate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWeather = true)

            when (val result = getCurrentWeatherUseCase(location.lat, location.lon)) {
                is Result.Success -> {
                    val newWeatherCard = WeatherCardData(location, result.data)
                    val updatedCards = _uiState.value.weatherCards.toMutableList()
                    val existingIndex = updatedCards.indexOfFirst { it.location.name == location.name }

                    if (existingIndex >= 0) {
                        updatedCards[existingIndex] = newWeatherCard
                    } else {
                        updatedCards.add(newWeatherCard)
                    }

                    _uiState.value = _uiState.value.copy(
                        weatherCards = updatedCards,
                        isLoadingWeather = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        errorMessage = "Unable to load weather data"
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        errorMessage = "Unknown error occurred"
                    )
                }
            }
        }
    }
}
