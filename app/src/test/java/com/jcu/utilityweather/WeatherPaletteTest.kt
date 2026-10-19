package com.jcu.utilityweather

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.jcu.utilityweather.domain.WeatherCategory
import com.jcu.utilityweather.ui.theme.weatherPalette
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherPaletteTest {

    /** WCAG contrast ratio between two colours. */
    private fun contrast(a: Color, b: Color): Float {
        val l1 = maxOf(a.luminance(), b.luminance())
        val l2 = minOf(a.luminance(), b.luminance())
        return (l1 + 0.05f) / (l2 + 0.05f)
    }

    @Test
    fun `every card palette has readable text in light and dark themes`() {
        for (darkTheme in listOf(false, true)) {
            for (category in WeatherCategory.entries) {
                val palette = weatherPalette(category, darkTheme)
                val ratio = contrast(palette.container, palette.content)
                assertTrue(
                    "$category (dark=$darkTheme) contrast $ratio is below the WCAG AA 4.5:1 minimum",
                    ratio >= 4.5f
                )
            }
        }
    }
}
