package com.jcu.utilityweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jcu.utilityweather.ui.navigation.UtilityApp
import com.jcu.utilityweather.ui.theme.UtilityWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtilityWeatherTheme {
                UtilityApp()
            }
        }
    }
}
