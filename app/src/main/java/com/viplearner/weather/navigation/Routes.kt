package com.viplearner.weather.navigation

import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object Routes {
    const val HOME = "home"
    const val SEARCH = "search/{lat}/{lng}/{locationName}"

    fun searchWithLocation(lat: Double, lng: Double, locationName: String = ""): String {
        val encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8.toString())
        return "search/$lat/$lng/$encodedLocationName"
    }
}
