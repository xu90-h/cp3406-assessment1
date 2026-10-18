package com.jcu.utilityweather.domain

/** A failure whose message is safe to show directly to the user. */
class WeatherException(message: String, cause: Throwable? = null) : Exception(message, cause)
