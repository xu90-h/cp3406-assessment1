package com.jcu.utilityweather.data.remote

import com.jcu.utilityweather.data.remote.dto.ForecastResponseDto
import com.jcu.utilityweather.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

private const val CURRENT_FIELDS =
    "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m"
private const val DAILY_FIELDS = "temperature_2m_max,temperature_2m_min"

/**
 * Both Open-Meteo endpoints are declared with absolute URLs so a single Retrofit
 * client (any base URL) can call both the geocoding and forecast hosts.
 */
interface WeatherApiService {

    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1
    ): GeocodingResponseDto

    @GET("https://api.open-meteo.com/v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("wind_speed_unit") windSpeedUnit: String,
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponseDto
}
