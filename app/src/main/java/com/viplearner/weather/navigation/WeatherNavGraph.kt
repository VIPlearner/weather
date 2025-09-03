package com.viplearner.weather.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import com.viplearner.weather.ui.screens.HomeView
import com.viplearner.weather.ui.screens.SearchScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeView(
                onNavigateToSearch = { lat, lng, locationName ->
                    navController.navigate(Routes.searchWithLocation(lat, lng, locationName))
                }
            )
        }

        composable(
            route = Routes.SEARCH,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType },
                navArgument("locationName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 37.4219999
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: -122.0862462
            val encodedLocationName = backStackEntry.arguments?.getString("locationName") ?: ""
            val locationName = URLDecoder.decode(encodedLocationName, StandardCharsets.UTF_8.toString())

            SearchScreen(
                currentLocation = LatLng(lat, lng),
                currentLocationName = locationName,
                onValueChange = { },
                onClickSearch = { }
            )
        }
    }
}
