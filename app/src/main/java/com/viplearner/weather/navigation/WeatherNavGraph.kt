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
                onNavigateToSearch = { lat, lng ->
                    navController.navigate(Routes.searchWithLocation(lat, lng))
                }
            )
        }

        composable(
            route = Routes.SEARCH,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 37.4219999
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: -122.0862462

            SearchScreen(
                currentLocation = LatLng(lat, lng),
                searchText = "",
                onValueChange = { },
                onClickSearch = { }
            )
        }
    }
}
