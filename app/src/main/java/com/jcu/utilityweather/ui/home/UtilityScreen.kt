package com.jcu.utilityweather.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcu.utilityweather.ui.WeatherScreenState
import com.jcu.utilityweather.ui.WeatherUiState
import com.jcu.utilityweather.ui.theme.UtilityWeatherTheme

/** Keeps content readable on tablets and in landscape instead of stretching edge to edge. */
private val MaxContentWidth = 480.dp

@Composable
fun UtilityScreen(
    state: WeatherScreenState,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        // Landscape is short, so search and weather sit side by side and the card gets the full height.
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CitySearchField(
                city = state.city,
                onCityChange = onCityChange,
                onSearch = onSearch,
                modifier = Modifier.weight(1f).padding(top = 8.dp)
            )
            WeatherPane(state = state, modifier = Modifier.weight(1f).fillMaxHeight())
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CitySearchField(
                city = state.city,
                onCityChange = onCityChange,
                onSearch = onSearch,
                modifier = Modifier.widthIn(max = MaxContentWidth)
            )
            WeatherPane(state = state, modifier = Modifier.fillMaxSize())
        }
    }
}

/** Scrollable so the expanded card is never clipped on short screens. */
@Composable
private fun WeatherPane(state: WeatherScreenState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherContent(state = state, modifier = Modifier.widthIn(max = MaxContentWidth))
    }
}

@Composable
private fun WeatherContent(state: WeatherScreenState, modifier: Modifier = Modifier) {
    when (val weatherState = state.weatherState) {
        is WeatherUiState.Idle -> Text(
            text = "Search a city to see its weather",
            modifier = modifier
        )
        is WeatherUiState.Loading -> CircularProgressIndicator(modifier = modifier)
        is WeatherUiState.Error -> Text(
            text = weatherState.message,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
        is WeatherUiState.Success -> WeatherGlanceCard(
            weather = weatherState.weather,
            settings = state.settings,
            modifier = modifier
        )
    }
}

@Composable
private fun CitySearchField(
    city: String,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = city,
        onValueChange = onCityChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("City") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        }
    )
}

private val successState = WeatherScreenState(weatherState = WeatherUiState.Success(previewWeather))

@Preview(showBackground = true, name = "Success")
@Composable
private fun UtilityScreenSuccessPreview() {
    UtilityWeatherTheme {
        UtilityScreen(state = successState, onCityChange = {}, onSearch = {})
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UtilityScreenDarkPreview() {
    UtilityWeatherTheme(darkTheme = true) {
        UtilityScreen(state = successState, onCityChange = {}, onSearch = {})
    }
}

@Preview(showBackground = true, name = "Landscape", device = "spec:width=891dp,height=411dp")
@Composable
private fun UtilityScreenLandscapePreview() {
    UtilityWeatherTheme {
        UtilityScreen(state = successState, onCityChange = {}, onSearch = {})
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun UtilityScreenLoadingPreview() {
    UtilityWeatherTheme {
        UtilityScreen(
            state = WeatherScreenState(weatherState = WeatherUiState.Loading),
            onCityChange = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun UtilityScreenErrorPreview() {
    UtilityWeatherTheme {
        UtilityScreen(
            state = WeatherScreenState(
                weatherState = WeatherUiState.Error("No internet connection. Check your connection and try again.")
            ),
            onCityChange = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true, name = "Idle")
@Composable
private fun UtilityScreenIdlePreview() {
    UtilityWeatherTheme {
        UtilityScreen(state = WeatherScreenState(), onCityChange = {}, onSearch = {})
    }
}
