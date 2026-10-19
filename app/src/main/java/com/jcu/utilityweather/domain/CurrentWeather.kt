package com.jcu.utilityweather.domain

data class CurrentWeather(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val todayMax: Double,
    val todayMin: Double,
    val weatherCode: Int
) {
    val condition: String get() = describeWeatherCode(weatherCode)
    val icon: String get() = iconForWeatherCode(weatherCode)
    val category: WeatherCategory get() = categoryForWeatherCode(weatherCode)
}

/** Broad groups of conditions, used to colour the weather card. */
enum class WeatherCategory { CLEAR, CLOUDY, FOG, RAIN, SNOW, STORM, UNKNOWN }

fun categoryForWeatherCode(code: Int): WeatherCategory = when (code) {
    0 -> WeatherCategory.CLEAR
    1, 2, 3 -> WeatherCategory.CLOUDY
    45, 48 -> WeatherCategory.FOG
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCategory.RAIN
    71, 73, 75, 77, 85, 86 -> WeatherCategory.SNOW
    95, 96, 99 -> WeatherCategory.STORM
    else -> WeatherCategory.UNKNOWN
}

/** Maps WMO weather codes (used by Open-Meteo) to a short human-readable label. */
private fun describeWeatherCode(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2, 3 -> "Partly cloudy"
    45, 48 -> "Foggy"
    51, 53, 55 -> "Drizzle"
    56, 57 -> "Freezing drizzle"
    61, 63, 65 -> "Rain"
    66, 67 -> "Freezing rain"
    71, 73, 75, 77 -> "Snow"
    80, 81, 82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95 -> "Thunderstorm"
    96, 99 -> "Thunderstorm with hail"
    else -> "Unknown"
}

private fun iconForWeatherCode(code: Int): String = when (code) {
    0 -> "☀️"
    1, 2, 3 -> "⛅"
    45, 48 -> "🌫️"
    51, 53, 55, 56, 57 -> "🌦️"
    61, 63, 65, 66, 67, 80, 81, 82 -> "🌧️"
    71, 73, 75, 77, 85, 86 -> "❄️"
    95, 96, 99 -> "⛈️"
    else -> "🌡️"
}
