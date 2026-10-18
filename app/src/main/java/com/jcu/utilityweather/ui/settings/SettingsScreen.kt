package com.jcu.utilityweather.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jcu.utilityweather.domain.TemperatureUnit
import com.jcu.utilityweather.domain.WindSpeedUnit
import com.jcu.utilityweather.domain.WeatherSettings

/** Controls that reshape what the weather screen shows. */
@Composable
fun SettingsScreen(
    settings: WeatherSettings,
    onSettingsChange: (WeatherSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.titleLarge)

        SettingSection(title = "Temperature unit") {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TemperatureUnit.entries.forEachIndexed { index, unit ->
                    SegmentedButton(
                        selected = settings.temperatureUnit == unit,
                        onClick = { onSettingsChange(settings.copy(temperatureUnit = unit)) },
                        shape = SegmentedButtonDefaults.itemShape(index, TemperatureUnit.entries.size)
                    ) {
                        Text(unit.symbol)
                    }
                }
            }
        }

        SettingSection(title = "Wind speed unit") {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                WindSpeedUnit.entries.forEachIndexed { index, unit ->
                    SegmentedButton(
                        selected = settings.windSpeedUnit == unit,
                        onClick = { onSettingsChange(settings.copy(windSpeedUnit = unit)) },
                        shape = SegmentedButtonDefaults.itemShape(index, WindSpeedUnit.entries.size)
                    ) {
                        Text(unit.label)
                    }
                }
            }
        }

        SettingSection(title = "Displayed details") {
            SwitchRow(
                label = "Show \"feels like\" temperature",
                checked = settings.showFeelsLike,
                onCheckedChange = { onSettingsChange(settings.copy(showFeelsLike = it)) }
            )
            SwitchRow(
                label = "Show wind speed",
                checked = settings.showWind,
                onCheckedChange = { onSettingsChange(settings.copy(showWind = it)) }
            )
        }
    }
}

@Composable
private fun SettingSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        content()
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, modifier = Modifier.padding(top = 12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
