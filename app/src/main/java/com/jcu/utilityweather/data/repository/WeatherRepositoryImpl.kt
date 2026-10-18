package com.jcu.utilityweather.data.repository

import com.jcu.utilityweather.data.remote.WeatherApiService
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WeatherException
import com.jcu.utilityweather.domain.WindSpeedUnit
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        city: String,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit
    ): Result<CurrentWeather> = try {
        Result.success(fetchWeather(city, temperatureUnit, windSpeedUnit))
    } catch (e: CancellationException) {
        throw e
    } catch (e: WeatherException) {
        Result.failure(e)
    } catch (e: SocketTimeoutException) {
        Result.failure(WeatherException("The request timed out. Please try again.", e))
    } catch (e: IOException) {
        Result.failure(WeatherException("No internet connection. Check your connection and try again.", e))
    } catch (e: HttpException) {
        Result.failure(
            WeatherException("The weather service is unavailable right now (error ${e.code()}). Please try again later.", e)
        )
    } catch (e: Exception) {
        Result.failure(WeatherException("Couldn't load the weather. Please try again.", e))
    }

    private suspend fun fetchWeather(
        city: String,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit
    ): CurrentWeather {
        val place = api.searchCity(name = city).results?.firstOrNull()
            ?: throw WeatherException("No city found matching \"$city\"")

        val forecast = api.getForecast(
            latitude = place.latitude,
            longitude = place.longitude,
            temperatureUnit = temperatureUnit.apiValue,
            windSpeedUnit = windSpeedUnit.apiValue
        )

        return CurrentWeather(
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
