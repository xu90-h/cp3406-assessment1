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
import com.jcu.utilityweather.ui.theme.UtilityWeatherTheme

private val sampleWeather = CurrentWeather(
    cityName = "Townsville, Australia",
    temperature = 24.0,
    feelsLike = 24.0,
    humidity = 63,
    windSpeed = 19.0,
    todayMax = 26.0,
    todayMin = 17.0,
    weatherCode = 2
)

@Composable
fun UtilityScreen(settings: WeatherSettings, modifier: Modifier = Modifier) {
    var city by rememberSaveable { mutableStateOf("Townsville") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CitySearchField(city = city, onCityChange = { city = it }, onSearch = {})

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherGlanceCard(weather = sampleWeather, settings = settings)
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
    val tempUnit = settings.temperatureUnit
    val windUnit = settings.windSpeedUnit
    var showDetails by rememberSaveable { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = weather.cityName, style = MaterialTheme.typography.titleLarge)
            Text(text = weather.icon, fontSize = 56.sp)
            Text(
                text = "${tempUnit.fromCelsius(weather.temperature).roundedString()}${tempUnit.symbol}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = weather.condition, style = MaterialTheme.typography.bodyLarge)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            StatColumn(
                label = "High / Low",
                value = "${tempUnit.fromCelsius(weather.todayMax).roundedString()}° / ${tempUnit.fromCelsius(weather.todayMin).roundedString()}°"
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
                        StatColumn(label = "Feels like", value = "${tempUnit.fromCelsius(weather.feelsLike).roundedString()}${tempUnit.symbol}")
                    }
                    StatColumn(label = "Humidity", value = "${weather.humidity}%")
                    if (settings.showWind) {
                        StatColumn(label = "Wind", value = "${windUnit.fromKmh(weather.windSpeed).roundedString()} ${windUnit.label}")
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
    UtilityWeatherTheme { UtilityScreen(settings = WeatherSettings()) }
}
