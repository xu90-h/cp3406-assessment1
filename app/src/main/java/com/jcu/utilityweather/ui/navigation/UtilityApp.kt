package com.jcu.utilityweather.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.jcu.utilityweather.ui.WeatherViewModel
import com.jcu.utilityweather.ui.home.UtilityScreen
import com.jcu.utilityweather.ui.settings.SettingsScreen

private enum class UtilityTab(val label: String) {
    Weather("Weather"),
    Settings("Settings")
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf(UtilityTab.Weather) }
    val viewModel: WeatherViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == UtilityTab.Weather,
                    onClick = { selectedTab = UtilityTab.Weather },
                    icon = { Icon(Icons.Filled.WbSunny, contentDescription = null) },
                    label = { Text(UtilityTab.Weather.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == UtilityTab.Settings,
                    onClick = { selectedTab = UtilityTab.Settings },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(UtilityTab.Settings.label) }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            UtilityTab.Weather -> UtilityScreen(
                state = state,
                onCityChange = viewModel::onCityChanged,
                onSearch = viewModel::onSearch,
                modifier = Modifier.padding(innerPadding)
            )
            UtilityTab.Settings -> SettingsScreen(
                settings = state.settings,
                onSettingsChange = viewModel::onSettingsChanged,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
