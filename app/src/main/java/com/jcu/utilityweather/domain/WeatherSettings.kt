package com.jcu.utilityweather.domain

enum class TemperatureUnit(val apiValue: String, val symbol: String) {
    CELSIUS("celsius", "°C"),
    FAHRENHEIT("fahrenheit", "°F")
}

enum class WindSpeedUnit(val apiValue: String, val label: String) {
    KMH("kmh", "km/h"),
    MPH("mph", "mph")
}

data class WeatherSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KMH,
    val showFeelsLike: Boolean = true,
    val showWind: Boolean = true
)
