package com.viplearner.weather.navigation

object Routes {
    const val HOME = "home"
    const val SEARCH = "search/{lat}/{lng}"

    fun searchWithLocation(lat: Double, lng: Double): String {
        return "search/$lat/$lng"
    }
}
