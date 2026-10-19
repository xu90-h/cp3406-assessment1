package com.jcu.utilityweather

import com.jcu.utilityweather.domain.WeatherCategory
import com.jcu.utilityweather.domain.categoryForWeatherCode
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherCategoryTest {

    @Test
    fun `weather codes map to the expected category`() {
        val expected = mapOf(
            0 to WeatherCategory.CLEAR,
            1 to WeatherCategory.CLOUDY, 2 to WeatherCategory.CLOUDY, 3 to WeatherCategory.CLOUDY,
            45 to WeatherCategory.FOG, 48 to WeatherCategory.FOG,
            51 to WeatherCategory.RAIN, 57 to WeatherCategory.RAIN, 65 to WeatherCategory.RAIN,
            67 to WeatherCategory.RAIN, 81 to WeatherCategory.RAIN,
            71 to WeatherCategory.SNOW, 77 to WeatherCategory.SNOW, 86 to WeatherCategory.SNOW,
            95 to WeatherCategory.STORM, 99 to WeatherCategory.STORM
        )

        expected.forEach { (code, category) ->
            assertEquals("code $code", category, categoryForWeatherCode(code))
        }
    }

    @Test
    fun `unrecognised codes fall back to unknown`() {
        assertEquals(WeatherCategory.UNKNOWN, categoryForWeatherCode(-1))
        assertEquals(WeatherCategory.UNKNOWN, categoryForWeatherCode(1234))
    }
}
