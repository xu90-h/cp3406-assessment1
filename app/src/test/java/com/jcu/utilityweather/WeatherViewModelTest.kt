package com.jcu.utilityweather

import com.jcu.utilityweather.data.repository.WeatherRepository
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.ui.WeatherUiState
import com.jcu.utilityweather.ui.WeatherViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository: WeatherRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful fetch updates state with weather`() = runTest {
        val weather = CurrentWeather(
            cityName = "Townsville, Australia",
            temperature = 28.0,
            feelsLike = 30.0,
            humidity = 60,
            windSpeed = 12.0,
            todayMax = 31.0,
            todayMin = 24.0,
            weatherCode = 0
        )
        coEvery { repository.getCurrentWeather(any(), any(), any()) } returns Result.success(weather)

        val viewModel = WeatherViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value.weatherState
        assertTrue(state is WeatherUiState.Success)
        assertEquals(weather, (state as WeatherUiState.Success).weather)
    }

    @Test
    fun `failed fetch updates state with error message`() = runTest {
        coEvery { repository.getCurrentWeather(any(), any(), any()) } returns
            Result.failure(NoSuchElementException("No city found matching \"Nowhereville\""))

        val viewModel = WeatherViewModel(repository)
        viewModel.onCityChanged("Nowhereville")
        viewModel.onSearch()
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value.weatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("No city found matching \"Nowhereville\"", (state as WeatherUiState.Error).message)
    }

    @Test
    fun `changing temperature unit triggers a refresh`() = runTest {
        val weather = CurrentWeather("Cairns, Australia", 82.0, 85.0, 55, 7.0, 88.0, 75.0, 1)
        coEvery { repository.getCurrentWeather(any(), any(), any()) } returns Result.success(weather)

        val viewModel = WeatherViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onTemperatureUnitChanged(TemperatureUnit.FAHRENHEIT)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.uiState.value.settings.temperatureUnit)
    }
}
