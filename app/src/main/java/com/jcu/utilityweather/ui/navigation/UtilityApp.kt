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
import androidx.hilt.navigation.compose.hiltViewModel
import com.jcu.utilityweather.ui.WeatherViewModel
import com.jcu.utilityweather.ui.home.UtilityScreen
import com.jcu.utilityweather.ui.settings.SettingsScreen

private enum class UtilityTab(val label: String) {
    Home("Weather"),
    Settings("Settings")
}

/**
 * Main scaffold with bottom navigation. Both destinations share one [WeatherViewModel]
 * instance (single Activity, no Navigation-Compose) so settings changes made on
 * [SettingsScreen] are reflected immediately on [UtilityScreen].
 */
@Composable
fun UtilityApp() {
    val viewModel: WeatherViewModel = hiltViewModel()
    var selectedTab by remember { mutableStateOf(UtilityTab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == UtilityTab.Home,
                    onClick = { selectedTab = UtilityTab.Home },
                    icon = { Icon(Icons.Filled.WbSunny, contentDescription = null) },
                    label = { Text(UtilityTab.Home.label) }
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
            UtilityTab.Home -> UtilityScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel
            )
            UtilityTab.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel
            )
        }
    }
}
