package com.jcu.utilityweather

import com.jcu.utilityweather.data.repository.WeatherRepository
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WindSpeedUnit
import com.jcu.utilityweather.ui.WeatherUiState
import com.jcu.utilityweather.ui.WeatherViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
    private val weather = CurrentWeather("Townsville, Australia", 28.0, 30.0, 60, 12.0, 31.0, 24.0, 0)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { repository.getCurrentWeather(any(), any(), any()) } returns Result.success(weather)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load shows weather for the default city`() = runTest(dispatcher) {
        val viewModel = WeatherViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value.weatherState
        assertTrue(state is WeatherUiState.Success)
        assertEquals(weather, (state as WeatherUiState.Success).weather)
    }

    @Test
    fun `failed search exposes the error message`() = runTest(dispatcher) {
        coEvery { repository.getCurrentWeather("Nowhereville", any(), any()) } returns
            Result.failure(NoSuchElementException("No city found matching \"Nowhereville\""))

        val viewModel = WeatherViewModel(repository)
        viewModel.onCityChanged("Nowhereville")
        viewModel.onSearch()
        advanceUntilIdle()

        val state = viewModel.uiState.value.weatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("No city found matching \"Nowhereville\"", (state as WeatherUiState.Error).message)
    }

    @Test
    fun `changing the temperature unit refetches in that unit`() = runTest(dispatcher) {
        val viewModel = WeatherViewModel(repository)
        advanceUntilIdle()

        viewModel.onSettingsChanged(
            viewModel.uiState.value.settings.copy(temperatureUnit = TemperatureUnit.FAHRENHEIT)
        )
        advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.getCurrentWeather(any(), TemperatureUnit.FAHRENHEIT, WindSpeedUnit.KMH)
        }
    }

    @Test
    fun `display toggles do not refetch`() = runTest(dispatcher) {
        val viewModel = WeatherViewModel(repository)
        advanceUntilIdle()

        viewModel.onSettingsChanged(viewModel.uiState.value.settings.copy(showWind = false))
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getCurrentWeather(any(), any(), any()) }
        assertEquals(false, viewModel.uiState.value.settings.showWind)
    }

    @Test
    fun `blank city does not trigger a search`() = runTest(dispatcher) {
        val viewModel = WeatherViewModel(repository)
        advanceUntilIdle()

        viewModel.onCityChanged("   ")
        viewModel.onSearch()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getCurrentWeather(any(), any(), any()) }
    }

    @Test
    fun `a slow earlier search cannot overwrite a newer result`() = runTest(dispatcher) {
        val slowResult = CompletableDeferred<Result<CurrentWeather>>()
        val older = weather.copy(cityName = "Slowtown")
        val newer = weather.copy(cityName = "Fastville")
        coEvery { repository.getCurrentWeather("Slowtown", any(), any()) } coAnswers { slowResult.await() }
        coEvery { repository.getCurrentWeather("Fastville", any(), any()) } returns Result.success(newer)

        val viewModel = WeatherViewModel(repository)
        advanceUntilIdle()

        viewModel.onCityChanged("Slowtown")
        viewModel.onSearch()
        runCurrent()
        viewModel.onCityChanged("Fastville")
        viewModel.onSearch()
        advanceUntilIdle()
        slowResult.complete(Result.success(older))
        advanceUntilIdle()

        val state = viewModel.uiState.value.weatherState
        assertTrue(state is WeatherUiState.Success)
        assertEquals("Fastville", (state as WeatherUiState.Success).weather.cityName)
    }
}
