package com.jcu.utilityweather.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.WeatherSettings
import com.jcu.utilityweather.ui.WeatherUiState
import com.jcu.utilityweather.ui.WeatherViewModel

/** Placeholder-turned-core screen: an at-a-glance view of the current city's weather. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CitySearchField(
            city = state.city,
            onCityChange = viewModel::onCityChanged,
            onSearch = viewModel::onSearch
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val weatherState = state.weatherState) {
                is WeatherUiState.Idle -> Text("Search a city to see its weather")
                is WeatherUiState.Loading -> CircularProgressIndicator()
                is WeatherUiState.Error -> Text(
                    text = weatherState.message,
                    color = MaterialTheme.colorScheme.error
                )
                is WeatherUiState.Success -> WeatherGlanceCard(
                    weather = weatherState.weather,
                    settings = state.settings
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySearchField(
    city: String,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = city,
        onValueChange = onCityChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("City") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        }
    )
}

@Composable
private fun WeatherGlanceCard(weather: CurrentWeather, settings: WeatherSettings) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = weather.cityName, style = MaterialTheme.typography.titleLarge)
            Text(text = weather.icon, fontSize = 56.sp)
            Text(
                text = "${weather.temperature.roundedString()}${settings.temperatureUnit.symbol}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = weather.condition, style = MaterialTheme.typography.bodyLarge)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatColumn(label = "H / L", value = "${weather.todayMax.roundedString()}° / ${weather.todayMin.roundedString()}°")
                if (settings.showFeelsLike) {
                    StatColumn(label = "Feels like", value = "${weather.feelsLike.roundedString()}${settings.temperatureUnit.symbol}")
                }
                StatColumn(label = "Humidity", value = "${weather.humidity}%")
                if (settings.showWind) {
                    StatColumn(label = "Wind", value = "${weather.windSpeed.roundedString()} ${settings.windSpeedUnit.label}")
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.SemiBold)
        Text(text = label, style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
    }
}

private fun Double.roundedString(): String = "%.0f".format(this)
