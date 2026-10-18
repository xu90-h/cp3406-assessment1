package com.jcu.utilityweather

import com.jcu.utilityweather.data.remote.WeatherApiService
import com.jcu.utilityweather.data.remote.dto.CurrentWeatherDto
import com.jcu.utilityweather.data.remote.dto.DailyWeatherDto
import com.jcu.utilityweather.data.remote.dto.ForecastResponseDto
import com.jcu.utilityweather.data.remote.dto.GeocodingResponseDto
import com.jcu.utilityweather.data.remote.dto.GeocodingResultDto
import com.jcu.utilityweather.data.repository.WeatherRepositoryImpl
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WindSpeedUnit
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class WeatherRepositoryImplTest {

    private val api: WeatherApiService = mockk()
    private val repository = WeatherRepositoryImpl(api)

    private val place = GeocodingResultDto("Townsville", -19.26, 146.8, "Australia", "Queensland")
    private val forecast = ForecastResponseDto(
        current = CurrentWeatherDto(temperature = 23.0, feelsLike = 25.0, humidity = 84, windSpeed = 15.0, weatherCode = 2),
        daily = DailyWeatherDto(maxTemperature = listOf(27.0), minTemperature = listOf(22.0))
    )

    private suspend fun fetch() =
        repository.getCurrentWeather("Townsville", TemperatureUnit.CELSIUS, WindSpeedUnit.KMH)

    @Test
    fun `maps geocoding and forecast into one weather model`() = runTest {
        coEvery { api.searchCity(any(), any()) } returns GeocodingResponseDto(listOf(place))
        coEvery { api.getForecast(any(), any(), any(), any(), any(), any(), any()) } returns forecast

        val weather = fetch().getOrThrow()

        assertEquals("Townsville, Australia", weather.cityName)
        assertEquals(23.0, weather.temperature, 0.0)
        assertEquals(27.0, weather.todayMax, 0.0)
        assertEquals(22.0, weather.todayMin, 0.0)
        assertEquals(84, weather.humidity)
    }

    @Test
    fun `requests the forecast in the chosen units`() = runTest {
        coEvery { api.searchCity(any(), any()) } returns GeocodingResponseDto(listOf(place))
        coEvery { api.getForecast(any(), any(), any(), any(), any(), any(), any()) } returns forecast

        repository.getCurrentWeather("Townsville", TemperatureUnit.FAHRENHEIT, WindSpeedUnit.MPH)

        coVerify {
            api.getForecast(-19.26, 146.8, any(), any(), "fahrenheit", "mph", any())
        }
    }

    @Test
    fun `unknown city gives a not found message`() = runTest {
        coEvery { api.searchCity(any(), any()) } returns GeocodingResponseDto(null)

        val error = fetch().exceptionOrNull()

        assertEquals("No city found matching \"Townsville\"", error?.message)
    }

    @Test
    fun `no network gives a friendly offline message`() = runTest {
        coEvery { api.searchCity(any(), any()) } throws UnknownHostException("geocoding-api.open-meteo.com")

        val error = fetch().exceptionOrNull()

        assertEquals("No internet connection. Check your connection and try again.", error?.message)
    }

    @Test
    fun `timeout gives a timeout message`() = runTest {
        coEvery { api.searchCity(any(), any()) } throws SocketTimeoutException()

        assertEquals("The request timed out. Please try again.", fetch().exceptionOrNull()?.message)
    }

    @Test
    fun `server error includes the status code`() = runTest {
        val response = Response.error<GeocodingResponseDto>(503, "".toResponseBody(null))
        coEvery { api.searchCity(any(), any()) } throws HttpException(response)

        val message = fetch().exceptionOrNull()?.message

        assertTrue(message!!.contains("503"))
    }
}
