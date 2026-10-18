package com.jcu.utilityweather.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponseDto(
    @Json(name = "current") val current: CurrentWeatherDto,
    @Json(name = "daily") val daily: DailyWeatherDto
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "apparent_temperature") val feelsLike: Double,
    @Json(name = "relative_humidity_2m") val humidity: Int,
    @Json(name = "wind_speed_10m") val windSpeed: Double,
    @Json(name = "weather_code") val weatherCode: Int
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "temperature_2m_max") val maxTemperature: List<Double>,
    @Json(name = "temperature_2m_min") val minTemperature: List<Double>
)
