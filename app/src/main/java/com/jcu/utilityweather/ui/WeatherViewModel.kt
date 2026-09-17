package com.jcu.utilityweather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcu.utilityweather.data.repository.WeatherRepository
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WeatherSettings
import com.jcu.utilityweather.domain.WindSpeedUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weather: CurrentWeather) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

data class WeatherScreenState(
    val city: String = "Townsville",
    val settings: WeatherSettings = WeatherSettings(),
    val weatherState: WeatherUiState = WeatherUiState.Idle
)

/**
 * Shared by both bottom-nav destinations (single Activity, no Navigation-Compose)
 * so a settings change can immediately re-fetch and reflect on the utility screen.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherScreenState())
    val uiState: StateFlow<WeatherScreenState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onCityChanged(city: String) {
        _uiState.update { it.copy(city = city) }
    }

    fun onSearch() = refresh()

    fun onTemperatureUnitChanged(unit: TemperatureUnit) {
        _uiState.update { it.copy(settings = it.settings.copy(temperatureUnit = unit)) }
        refresh()
    }

    fun onWindSpeedUnitChanged(unit: WindSpeedUnit) {
        _uiState.update { it.copy(settings = it.settings.copy(windSpeedUnit = unit)) }
        refresh()
    }

    fun onShowFeelsLikeChanged(show: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(showFeelsLike = show)) }
    }

    fun onShowWindChanged(show: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(showWind = show)) }
    }

    private fun refresh() {
        val state = _uiState.value
        if (state.city.isBlank()) return

        _uiState.update { it.copy(weatherState = WeatherUiState.Loading) }
        viewModelScope.launch {
            repository.getCurrentWeather(
                city = state.city,
                temperatureUnit = state.settings.temperatureUnit,
                windSpeedUnit = state.settings.windSpeedUnit
            ).onSuccess { weather ->
                _uiState.update { it.copy(weatherState = WeatherUiState.Success(weather)) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(weatherState = WeatherUiState.Error(error.message ?: "Something went wrong"))
                }
            }
        }
    }
}
