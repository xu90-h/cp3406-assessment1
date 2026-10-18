package com.jcu.utilityweather.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.WeatherSettings
import com.jcu.utilityweather.ui.WeatherScreenState
import com.jcu.utilityweather.ui.WeatherUiState
import com.jcu.utilityweather.ui.theme.UtilityWeatherTheme

@Composable
fun UtilityScreen(
    state: WeatherScreenState,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CitySearchField(city = state.city, onCityChange = onCityChange, onSearch = onSearch)

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
    val tempSymbol = settings.temperatureUnit.symbol
    var showDetails by rememberSaveable { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = weather.cityName, style = MaterialTheme.typography.titleLarge)
            Text(text = weather.icon, fontSize = 56.sp)
            Text(
                text = "${weather.temperature.roundedString()}$tempSymbol",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = weather.condition, style = MaterialTheme.typography.bodyLarge)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            StatColumn(
                label = "High / Low",
                value = "${weather.todayMax.roundedString()}° / ${weather.todayMin.roundedString()}°"
            )

            TextButton(onClick = { showDetails = !showDetails }) {
                Text(if (showDetails) "Hide details" else "Show details")
                Icon(
                    imageVector = if (showDetails) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = showDetails) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    if (settings.showFeelsLike) {
                        StatColumn(label = "Feels like", value = "${weather.feelsLike.roundedString()}$tempSymbol")
                    }
                    StatColumn(label = "Humidity", value = "${weather.humidity}%")
                    if (settings.showWind) {
                        StatColumn(label = "Wind", value = "${weather.windSpeed.roundedString()} ${settings.windSpeedUnit.label}")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.SemiBold)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun Double.roundedString(): String = "%.0f".format(this)

@Preview(showBackground = true)
@Composable
private fun UtilityScreenPreview() {
    UtilityWeatherTheme {
        UtilityScreen(
            state = WeatherScreenState(
                weatherState = WeatherUiState.Success(
                    CurrentWeather("Townsville, Australia", 24.0, 24.0, 63, 19.0, 26.0, 17.0, 2)
                )
            ),
            onCityChange = {},
            onSearch = {}
        )
    }
}
