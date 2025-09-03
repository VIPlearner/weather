package com.viplearner.weather.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.viplearner.weather.domain.model.Weather
import com.viplearner.weather.utils.toRoundedInt
import kotlin.math.roundToInt

@Composable
fun HomeView(
    onNavigateToSearch: (Double, Double) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity
    val locationState = remember { mutableStateOf<Location?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            permissionGranted = granted
        }
    )

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            permissionGranted = true
        }
    }

    LaunchedEffect(permissionGranted) {
        Log.d("t", "Loke loke $permissionGranted")
        if (permissionGranted && activity != null) {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationClient.lastLocation.addOnSuccessListener{ location: Location? ->
                Log.d("t", "Found location")
                location?.let {
                    locationState.value = it
                    viewModel.fetchWeatherForLocation(it.latitude, it.longitude)
                }
            }.addOnFailureListener { p0 ->
                Log.d("try", "Failure finding location")
            }.addOnCompleteListener {
                Log.d("why", "Done loading")
            }
        }
    }

    val weather by viewModel.weatherState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    HomeScreen(
        weather = weather,
        permissionGranted = permissionGranted,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshWeather() },
        onClickSearch = {
            locationState.value?.let { location ->
                onNavigateToSearch(location.latitude, location.longitude)
            }
        }
    )

}
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    weather: Weather?,
    permissionGranted: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onClickSearch: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2E63),
            Color(0xFF1D3A74),
            Color(0xFF2C4E74)
        )
    )

    when {
        !permissionGranted -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = backgroundGradient)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Location permission required",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        weather == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = backgroundGradient)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        }

        else -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(brush = backgroundGradient)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        Topbar(cityName = weather.cityName)
                    },
                    bottomBar = {
                        BottomBar(onClickSearch = onClickSearch)
                    }
                ) { paddingValues ->
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = onRefresh,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                        ) {
                            Text(
                                text = "${weather.temperature.toRoundedInt()}°",
                                style = MaterialTheme.typography.displayLarge.copy(color = Color.White)
                            )
                            Text(
                                text = weather.description,
                                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                            )
                            Spacer(Modifier.height(30.dp))
                            Text(
                                text = "H: ${weather.high}°  L: ${weather.low}°",
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)
                            )
                            Text(
                                text = "Feels like ${weather.feelsLike}°",
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Topbar(
    modifier: Modifier = Modifier,
    cityName: String
) {
    Row(
        modifier = modifier
            .background(Color.Transparent)
            .padding(16.dp)
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = "Menu",
            tint = Color.White
        )

        Text(
            text = cityName,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
        // You can add more icons or buttons here if needed
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    onClickSearch: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onClickSearch
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val refreshThreshold = with(density) { 80.dp.toPx() }

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var isTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullOffset = 0f
            isTriggered = false
        }
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (pullOffset >= refreshThreshold && !isRefreshing) {
                            isTriggered = true
                            onRefresh()
                        } else {
                            pullOffset = 0f
                        }
                    }
                ) { _, dragAmount ->
                    if (dragAmount.y > 0 && !isRefreshing) {
                        pullOffset = (pullOffset + dragAmount.y * 0.5f).coerceAtLeast(0f)
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier.offset {
                IntOffset(0, if (isRefreshing) refreshThreshold.roundToInt() else pullOffset.roundToInt())
            }
        ) {
            content()
        }

        if (pullOffset > 0 || isRefreshing) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(40.dp)
                    .offset { IntOffset(0, (pullOffset * 0.8f).roundToInt()) },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF1A2E63)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF1A2E63),
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(pullOffset / refreshThreshold * 180f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        weather = Weather(
            cityName = "Lagos",
            temperature = 30.0,
            high = 32.0,
            low = 28.0,
            description = "Partly Cloudy",
            humidity = 70,
            windSpeed = 5.0,
            feelsLike = 32.0,
            pressure = 1010,
            visibility = 10_000
        ),
        permissionGranted = true,
        isRefreshing = false,
        onRefresh = {},
        onClickSearch = {}
    )
}