package com.jcu.utilityweather.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcu.utilityweather.domain.CurrentWeather
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WeatherSettings
import com.jcu.utilityweather.domain.WindSpeedUnit
import com.jcu.utilityweather.ui.theme.UtilityWeatherTheme

/**
 * The at-a-glance card: key facts are always visible, the rest sits behind a
 * "Show details" toggle so the main screen stays uncluttered.
 */
@Composable
fun WeatherGlanceCard(
    weather: CurrentWeather,
    settings: WeatherSettings,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    var showDetails by rememberSaveable { mutableStateOf(initiallyExpanded) }
    // Landscape screens are short, so the card is laid out more tightly there.
    val compact = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(if (compact) 12.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherSummary(weather = weather, settings = settings, compact = compact)

            HorizontalDivider(modifier = Modifier.padding(vertical = if (compact) 8.dp else 16.dp))

            StatColumn(
                label = "High / Low",
                value = "${weather.todayMax.roundedString()}° / ${weather.todayMin.roundedString()}°"
            )

            DetailsToggle(expanded = showDetails, onToggle = { showDetails = !showDetails })

            AnimatedVisibility(visible = showDetails) {
                DetailsRow(weather = weather, settings = settings)
            }
        }
    }
}

@Composable
private fun WeatherSummary(weather: CurrentWeather, settings: WeatherSettings, compact: Boolean) {
    val temperature = "${weather.temperature.roundedString()}${settings.temperatureUnit.symbol}"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = weather.cityName, style = MaterialTheme.typography.titleLarge)
        if (compact) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = weather.icon, fontSize = 40.sp)
                Text(text = temperature, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(text = weather.icon, fontSize = 56.sp)
            Text(text = temperature, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = weather.condition, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun DetailsToggle(expanded: Boolean, onToggle: () -> Unit) {
    TextButton(onClick = onToggle) {
        Text(if (expanded) "Hide details" else "Show details")
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = null
        )
    }
}

@Composable
private fun DetailsRow(weather: CurrentWeather, settings: WeatherSettings) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        if (settings.showFeelsLike) {
            StatColumn(
                label = "Feels like",
                value = "${weather.feelsLike.roundedString()}${settings.temperatureUnit.symbol}"
            )
        }
        StatColumn(label = "Humidity", value = "${weather.humidity}%")
        if (settings.showWind) {
            StatColumn(
                label = "Wind",
                value = "${weather.windSpeed.roundedString()} ${settings.windSpeedUnit.label}"
            )
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

internal val previewWeather = CurrentWeather(
    cityName = "Townsville, Australia",
    temperature = 24.0,
    feelsLike = 25.0,
    humidity = 63,
    windSpeed = 19.0,
    todayMax = 26.0,
    todayMin = 17.0,
    weatherCode = 2
)

@Preview(showBackground = true, name = "Collapsed")
@Composable
private fun WeatherGlanceCardPreview() {
    UtilityWeatherTheme {
        WeatherGlanceCard(weather = previewWeather, settings = WeatherSettings(), modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, name = "Expanded, Fahrenheit and mph")
@Composable
private fun WeatherGlanceCardExpandedPreview() {
    UtilityWeatherTheme {
        WeatherGlanceCard(
            weather = previewWeather.copy(temperature = 75.0, feelsLike = 77.0, todayMax = 79.0, todayMin = 63.0, windSpeed = 12.0),
            settings = WeatherSettings(
                temperatureUnit = TemperatureUnit.FAHRENHEIT,
                windSpeedUnit = WindSpeedUnit.MPH
            ),
            modifier = Modifier.padding(16.dp),
            initiallyExpanded = true
        )
    }
}
