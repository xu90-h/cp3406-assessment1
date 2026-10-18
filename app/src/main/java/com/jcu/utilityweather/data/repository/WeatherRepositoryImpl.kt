package com.jcu.utilityweather.data.repository

import com.jcu.utilityweather.data.remote.WeatherApiService
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WindSpeedUnit
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        city: String,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit
    ): Result<CurrentWeather> = runCatching {
        val place = api.searchCity(name = city).results?.firstOrNull()
            ?: throw NoSuchElementException("No city found matching \"$city\"")

        val forecast = api.getForecast(
            latitude = place.latitude,
            longitude = place.longitude,
            temperatureUnit = temperatureUnit.apiValue,
            windSpeedUnit = windSpeedUnit.apiValue
        )

        CurrentWeather(
            cityName = listOfNotNull(place.name, place.country).joinToString(", "),
            temperature = forecast.current.temperature,
            feelsLike = forecast.current.feelsLike,
            humidity = forecast.current.humidity,
            windSpeed = forecast.current.windSpeed,
            todayMax = forecast.daily.maxTemperature.firstOrNull() ?: forecast.current.temperature,
            todayMin = forecast.daily.minTemperature.firstOrNull() ?: forecast.current.temperature,
            weatherCode = forecast.current.weatherCode
        )
    }
}
