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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcu.utilityweather.ui.WeatherViewModel
import com.jcu.utilityweather.ui.home.UtilityScreen
import com.jcu.utilityweather.ui.settings.SettingsScreen

private enum class UtilityTab(val label: String, val icon: ImageVector) {
    Weather("Weather", Icons.Filled.WbSunny),
    Settings("Settings", Icons.Filled.Settings)
}

/**
 * App scaffold: a bottom navigation bar switching between the weather and settings screens.
 * Both screens share one [WeatherViewModel], so a settings change shows up straight away.
 */
@Composable
fun UtilityApp() {
    val viewModel: WeatherViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(UtilityTab.Weather) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                UtilityTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) }
                    )
                }
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
