package com.jcu.utilityweather.data.repository

import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WindSpeedUnit
import kotlinx.coroutines.delay

/** Offline stand-in used until the real web API is connected. */
class FakeWeatherRepository : WeatherRepository {

    override suspend fun getCurrentWeather(
        city: String,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit
    ): Result<CurrentWeather> {
        delay(500)
        if (city.trim().equals("Nowhere", ignoreCase = true)) {
            return Result.failure(NoSuchElementException("No city found matching \"$city\""))
        }
        return Result.success(
            CurrentWeather(
                cityName = city.trim(),
                temperature = temperatureUnit.fromCelsius(24.0),
                feelsLike = temperatureUnit.fromCelsius(24.0),
                humidity = 63,
                windSpeed = windSpeedUnit.fromKmh(19.0),
                todayMax = temperatureUnit.fromCelsius(26.0),
                todayMin = temperatureUnit.fromCelsius(17.0),
                weatherCode = 2
            )
        )
    }
}
