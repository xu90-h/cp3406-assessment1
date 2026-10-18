package com.jcu.utilityweather.domain

enum class TemperatureUnit(val apiValue: String, val symbol: String) {
    CELSIUS("celsius", "°C"),
    FAHRENHEIT("fahrenheit", "°F");

    fun fromCelsius(celsius: Double): Double =
        if (this == CELSIUS) celsius else celsius * 9 / 5 + 32
}

enum class WindSpeedUnit(val apiValue: String, val label: String) {
    KMH("kmh", "km/h"),
    MPH("mph", "mph");

    fun fromKmh(kmh: Double): Double =
        if (this == KMH) kmh else kmh * 0.621371
}

data class WeatherSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KMH,
    val showFeelsLike: Boolean = true,
    val showWind: Boolean = true
)
