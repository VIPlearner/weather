package com.viplearner.weather.utils

import com.viplearner.weather.domain.model.CityCoordinate
import kotlin.math.round

fun Double.toRoundedInt(): Int {
    return round(this).toInt()
}

fun CityCoordinate.toFormattedString(): String {
    val parts = mutableListOf(name)

    if (state.isNotBlank()) {
        parts.add(state)
    }

    if (country.isNotBlank()) {
        parts.add(country)
    }

    return parts.joinToString(", ")
}
