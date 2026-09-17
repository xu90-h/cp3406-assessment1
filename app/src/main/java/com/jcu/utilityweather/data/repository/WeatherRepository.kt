package com.jcu.utilityweather.data.repository

import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WindSpeedUnit

interface WeatherRepository {
    suspend fun getCurrentWeather(
        city: String,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit
    ): Result<CurrentWeather>
}
