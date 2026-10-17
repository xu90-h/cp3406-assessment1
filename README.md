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


---

# Development log


### Week 1 setup log
- Installed Android Studio (Quail 4 | 2026.1.4) on macOS (Apple Silicon)
- Completed the Introduction to Kotlin Pathway
- Created this private repository to track assessment work
- Reviewed Assessment 1 (Utility App), Assessment 2 (Ethics Presentation),
  and Assessment 3 (Education App) briefs


### Assessment 1 - Utility App direction notes (Week 1)

Possible directions considered:
1. Water intake tracker — main screen shows glasses/ml drunk today
   vs. a daily goal; settings screen lets user adjust the daily goal
2. Weather at-a-glance — current temp, condition, one-line summary;
   settings screen for city/unit
3. Focus timer (Pomodoro-style) — countdown on main screen;
   settings screen for session length

Leaning toward: #1 (Water intake tracker)

Open question for clarification:
- The spec says "settings do not need to be persistent" — does this
  mean settings can reset on app restart, or also reset when
  switching between the main and settings screens?

### Week 2 - Compose layout basics practical

- Worked through the Add a Button to an App pathway and the Intro to
  State in Compose codelab (Android Basics with Compose, Unit 2)
- Experiment note: changed the weather glance card's stat `Row` from
  `Arrangement.SpaceBetween` to `Arrangement.SpaceEvenly` and observed
  the stat columns spread out evenly instead of hugging the edges —
  much easier to scan at a glance

#### Connecting to the Utility App (Assessment 1)

Went with direction #2 (Weather at-a-glance) instead of the water
tracker, and started building it as `UtilityWeatherApp`:

- **Main screen** (`UtilityScreen.kt`) — a city search field at the
  top, and below it a `WeatherGlanceCard` showing city name, condition
  icon, current temperature, and a row of stats (high/low, feels like,
  humidity, wind). Loading/error/idle states are handled with a
  `when` over `WeatherUiState`, so the screen communicates useful
  information at a glance without extra taps.
- **Settings screen** (`SettingsScreen.kt`) — segmented buttons to pick
  the temperature unit and wind speed unit, plus switches to toggle
  whether "feels like" and wind speed show on the main screen.
- **Simple, direct interaction**: typing a city and tapping the search
  icon (`CitySearchField`) is the one core interaction — a single
  text field and one button, with the result rendered immediately
  below.
- Settings are held in `WeatherViewModel` state (via `WeatherSettings`)
  rather than persisted, in line with the "settings do not need to be
  persistent" note from Week 1.

Question to raise with the lecturer/tutor:
- Is showing 4 stat values (H/L, feels like, humidity, wind) still
  "at a glance", or should some of these move behind a "details"
  expansion to keep the main screen simpler?
