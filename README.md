# Utility Weather

An "at-a-glance" weather utility app built for CP3406/CP5307 Assessment 1. Search any city
and instantly see its current temperature, condition, and the details that matter most —
no clutter, no extra taps.

## Core features

- **Current weather at a glance** — city name, temperature, condition icon, today's
  high/low, feels-like temperature, humidity, and wind speed, all on one screen.
- **City search** — type any city name and fetch its live weather via the free
  [Open-Meteo](https://open-meteo.com/) geocoding + forecast APIs (no API key required).
- **Settings that reshape the main screen** — switch between Celsius/Fahrenheit and
  km/h/mph, and toggle whether "feels like" and wind speed are shown. Changes apply
  immediately on the utility screen (settings are session-only, not persisted).
- **Bottom navigation** between the Weather screen and the Settings screen, all within a
  single Activity.

## Architecture

- **Jetpack Compose** + **Material 3** for the entire UI.
- **MVVM**: a single `WeatherViewModel` (shared by both screens via Hilt) exposes a
  `StateFlow<WeatherScreenState>` that both screens collect.
- **Repository pattern**: `WeatherRepository` abstracts the data source; `WeatherRepositoryImpl`
  talks to Retrofit. The ViewModel never touches the network directly.
- **Dependency injection**: Hilt provides the Retrofit/OkHttp/Moshi stack and binds the
  repository interface to its implementation (`di/NetworkModule.kt`, `di/RepositoryModule.kt`).
- **Networking**: Retrofit + Moshi call Open-Meteo's geocoding endpoint (city name → lat/lon)
  and forecast endpoint (lat/lon → current + daily weather), combined into one domain model
  (`CurrentWeather`) by the repository.

## Project structure

```
app/src/main/java/com/jcu/utilityweather/
├── data/
│   ├── remote/            Retrofit service + DTOs
│   └── repository/        WeatherRepository interface + impl
├── di/                    Hilt modules (networking, repository binding)
├── domain/                Domain models (CurrentWeather, WeatherSettings)
├── ui/
│   ├── WeatherViewModel.kt
│   ├── home/              UtilityScreen (main weather display)
│   ├── settings/          SettingsScreen
│   ├── navigation/        UtilityApp (bottom-nav scaffold)
│   └── theme/             Material 3 theme
└── MainActivity.kt
```

## Running the app

Open the project in Android Studio (Ladybug or later), let Gradle sync, and run on an
emulator or device with internet access — no API key setup needed.

## Tests

`app/src/test/java/.../WeatherViewModelTest.kt` covers the ViewModel's success/error states
and confirms a settings change triggers a refresh, using MockK to fake the repository.
