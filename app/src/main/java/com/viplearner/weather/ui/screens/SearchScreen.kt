package com.viplearner.weather.ui.screens

import SearchTextField
import SearchTextField
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.viplearner.weather.domain.model.CityCoordinate
import com.viplearner.weather.utils.toRoundedInt
import kotlinx.coroutines.launch

const val DEFAULT_ZOOM = 8f

@Composable
fun SearchScreen(
    currentLocation: LatLng,
    onValueChange: (String) -> Unit,
    onClickSearch: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, DEFAULT_ZOOM)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.selectLocation(
            CityCoordinate(
                name = "",
                lat = currentLocation.latitude,
                lon = currentLocation.longitude,
                country = "",
                state = ""
            )
        )
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            Box(
                modifier = Modifier.padding(20.dp)
            ) {
                SearchTextField(
                    searchMode = uiState.isSearching,
                    value = uiState.searchQuery,
                    onValueChange = { query ->
                        viewModel.updateSearchQuery(query)
                        onValueChange(query)
                    },
                    onClickPinpoint = {
                        scope.launch{
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM)
                            )
                        }
                    },
                    onClickSearch = { searchMode ->
                        viewModel.setSearchMode(searchMode)
                    },
                    onKeyboardSearch = {
                        viewModel.searchAllResults()
                    }
                )

                SearchSuggestionsDropdown(
                    expanded = uiState.isSearching && (uiState.searchSuggestions.isNotEmpty() || uiState.isLoadingSuggestions),
                    suggestions = uiState.searchSuggestions,
                    isLoading = uiState.isLoadingSuggestions,
                    onSuggestionClick = { suggestion ->
                        viewModel.selectLocation(suggestion)
                        selectedLocation = LatLng(suggestion.lat, suggestion.lon)
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(suggestion.lat, suggestion.lon),
                                    DEFAULT_ZOOM
                                )
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val pagerState = rememberPagerState(
                pageCount = {
                    if (uiState.isLoadingWeather) 1 else uiState.weatherCards.size
                }
            )

            LaunchedEffect(pagerState.currentPage) {
                if (uiState.weatherCards.isNotEmpty() && pagerState.currentPage < uiState.weatherCards.size) {
                    val currentWeatherCard = uiState.weatherCards[pagerState.currentPage]
                    selectedLocation = LatLng(currentWeatherCard.location.lat, currentWeatherCard.location.lon)
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(currentWeatherCard.location.lat, currentWeatherCard.location.lon),
                            DEFAULT_ZOOM
                        )
                    )
                }
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .clip(RoundedCornerShape(16.dp)),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                ),
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    val cityCoordinate = CityCoordinate(
                        name = "Selected Location",
                        lat = latLng.latitude,
                        lon = latLng.longitude,
                        country = "",
                        state = ""
                    )
                    viewModel.selectLocation(cityCoordinate)
                    onClickSearch("${latLng.latitude},${latLng.longitude}")
                }
            ) {
                if (!uiState.isLoadingWeather) {
                    uiState.weatherCards.forEachIndexed { index, weatherCard ->
                        val isActive = pagerState.currentPage == index
                        Marker(
                            state = MarkerState(
                                position = LatLng(weatherCard.location.lat, weatherCard.location.lon)
                            ),
                            title = weatherCard.location.name,
                            snippet = "${weatherCard.weather.temperature.toRoundedInt()}°C - ${weatherCard.weather.description}",
                            icon = if (isActive)
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            else
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                    }
                }

                selectedLocation?.let { location ->
                    if (!uiState.isLoadingWeather && uiState.weatherCards.none { LatLng(it.location.lat, it.location.lon) == location }) {
                        Marker(
                            state = MarkerState(position = location),
                            title = "Selected Location",
                            snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                        )
                    }
                }
            }

            if (uiState.isLoadingWeather || uiState.weatherCards.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 16.dp,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 16.dp)
                ) { pageIndex ->
                    if (uiState.isLoadingWeather) {
                        LoadingWeatherCard()
                    } else {
                        WeatherCard(
                            weatherData = uiState.weatherCards[pageIndex],
                            onClick = { clickedWeatherCard ->
                                selectedLocation = LatLng(clickedWeatherCard.location.lat, clickedWeatherCard.location.lon)
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(clickedWeatherCard.location.lat, clickedWeatherCard.location.lon),
                                            DEFAULT_ZOOM
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

}