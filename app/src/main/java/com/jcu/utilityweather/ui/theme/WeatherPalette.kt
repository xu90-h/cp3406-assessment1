package com.jcu.utilityweather.ui.theme

import androidx.compose.ui.graphics.Color
import com.jcu.utilityweather.domain.WeatherCategory

/** Card background and the text colour used on it. */
data class WeatherPalette(val container: Color, val content: Color)

/**
 * Hand-picked pairs so every condition keeps readable text in both themes
 * (the contrast ratio is checked in WeatherPaletteTest). Light backgrounds for cloud, fog and
 * snow are deliberately not too pale, otherwise their whitish emoji icons blend into the card.
 */
fun weatherPalette(category: WeatherCategory, darkTheme: Boolean): WeatherPalette =
    if (darkTheme) darkPalette.getValue(category) else lightPalette.getValue(category)

private val lightPalette = mapOf(
    WeatherCategory.CLEAR to WeatherPalette(Color(0xFFFFF0C2), Color(0xFF3A2D00)),
    WeatherCategory.CLOUDY to WeatherPalette(Color(0xFFBCCDE2), Color(0xFF111D2B)),
    WeatherCategory.FOG to WeatherPalette(Color(0xFFCCCCCC), Color(0xFF1C1C1C)),
    WeatherCategory.RAIN to WeatherPalette(Color(0xFFCFE2F6), Color(0xFF0C2640)),
    WeatherCategory.SNOW to WeatherPalette(Color(0xFFBFDCEE), Color(0xFF0F2634)),
    WeatherCategory.STORM to WeatherPalette(Color(0xFFD9D2EC), Color(0xFF221A3B)),
    WeatherCategory.UNKNOWN to WeatherPalette(Color(0xFFE4E4EA), Color(0xFF22222A))
)

private val darkPalette = mapOf(
    WeatherCategory.CLEAR to WeatherPalette(Color(0xFF4D3E0E), Color(0xFFFFEBB0)),
    WeatherCategory.CLOUDY to WeatherPalette(Color(0xFF2B3745), Color(0xFFDDE7F2)),
    WeatherCategory.FOG to WeatherPalette(Color(0xFF3A3A3A), Color(0xFFE8E8E8)),
    WeatherCategory.RAIN to WeatherPalette(Color(0xFF1E3954), Color(0xFFD0E4F9)),
    WeatherCategory.SNOW to WeatherPalette(Color(0xFF2D4557), Color(0xFFE4F2FB)),
    WeatherCategory.STORM to WeatherPalette(Color(0xFF362D55), Color(0xFFE5DDF9)),
    WeatherCategory.UNKNOWN to WeatherPalette(Color(0xFF34343C), Color(0xFFE6E6EE))
)
