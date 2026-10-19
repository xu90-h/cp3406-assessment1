# Utility Weather

An "at-a-glance" weather utility app built for CP3406/CP5307 Assessment 1. Search any city
and instantly see its current temperature, condition and today's high/low. Everything else
sits one tap away, so the main screen stays uncluttered.

## Core features

- **Current weather at a glance** — city name, condition icon, temperature and today's
  high/low are shown immediately. Feels-like temperature, humidity and wind speed sit
  behind an expandable "Show details" toggle.
- **City search** — type any city name to fetch its live weather from the free
  [Open-Meteo](https://open-meteo.com/) geocoding and forecast APIs (no API key needed).
- **Settings that reshape the main screen** — switch between °C/°F and km/h/mph, and
  choose whether "feels like" and wind speed appear in the details. Changes apply
  immediately; unit changes re-fetch the data in the new unit.
- **Condition-tinted card** — the weather card changes colour with the conditions (clear, cloudy,
  fog, rain, snow, storm) in both light and dark mode. Every colour pair is checked in a unit test
  to keep text at or above the WCAG AA 4.5:1 contrast ratio.
- **Friendly error messages** — offline, timeout, server error and unknown-city cases each
  show a clear message instead of a raw exception.
- **Responsive layout** — content scrolls on short screens and is capped to a readable
  width; landscape switches to a side-by-side search/weather layout with a compact card.
- **Bottom navigation** between the Weather and Settings screens within a single Activity.

## How the features map to the course topics

| Topic | Where it is used |
|---|---|
| Week 1 — Kotlin and Android Studio | Kotlin throughout; Gradle version catalog (`gradle/libs.versions.toml`) |
| Week 2 — Layouts with Jetpack Compose | `UtilityApp`, `UtilityScreen`, `WeatherGlanceCard`; scrolling, landscape layout, `@Preview`s |
| Week 3 — Material 3 | Material 3 theme, `NavigationBar`, `Card`, segmented buttons, switches, dynamic colour, condition-based card colours (`WeatherPalette`) |
| Week 4 — ViewModel, DI, Repository | `WeatherViewModel` + `StateFlow`, `WeatherRepository`, Hilt modules |
| Week 5 — Web APIs with Retrofit | `WeatherApiService` (Retrofit + Moshi + OkHttp) calling Open-Meteo |

## Technology and requirements

| | |
|---|---|
| Language / UI | Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), Material 3 |
| Architecture | MVVM, Repository pattern, Hilt 2.52 |
| Networking | Retrofit 2.11, Moshi 1.15, OkHttp 4.12 |
| Android | minSdk 26, compileSdk / targetSdk 35 |
| Build | Android Gradle Plugin 8.7.2, Gradle 8.10.2, JDK 17+ |
| Testing | JUnit 4, MockK, kotlinx-coroutines-test |

An internet connection is required at runtime.

## Architecture

```
UtilityScreen / SettingsScreen  ──►  WeatherViewModel  ──►  WeatherRepository
        (Compose, stateless)         (StateFlow state)      (interface)
                                                                 │
                                                       WeatherRepositoryImpl
                                                                 │
                                                       WeatherApiService (Retrofit)
                                                                 │
                                                             Open-Meteo
```

- **MVVM** — a single `WeatherViewModel` is shared by both bottom-navigation screens via
  Hilt. It exposes one `StateFlow<WeatherScreenState>` (city, settings and a
  loading / success / error state). The screens are stateless and only render that state.
- **Repository pattern** — `WeatherRepository` hides where the data comes from. The ViewModel
  never touches the network directly, which is also what makes it easy to unit test.
- **Dependency injection** — Hilt provides Retrofit, OkHttp and Moshi (`di/NetworkModule.kt`) and
  binds the repository interface to its implementation (`di/RepositoryModule.kt`).
- **Networking** — the repository calls the geocoding endpoint (city name to latitude/longitude),
  then the forecast endpoint (current conditions and today's high/low in the chosen units), and
  merges them into one `CurrentWeather` model.
- **Error handling** — `WeatherRepositoryImpl` converts failures (no network, timeout, HTTP error,
  unknown city) into a `WeatherException` whose message is safe to show to the user. Coroutine
  cancellation is rethrown, not swallowed.

## Project structure

```
app/src/main/java/com/jcu/utilityweather/
├── data/
│   ├── remote/            Retrofit service and DTOs
│   └── repository/        WeatherRepository interface and implementation
├── di/                    Hilt modules (networking, repository binding)
├── domain/                CurrentWeather, WeatherSettings, WeatherException
├── ui/
│   ├── WeatherViewModel.kt
│   ├── home/              UtilityScreen and WeatherGlanceCard
│   ├── settings/          SettingsScreen
│   ├── navigation/        UtilityApp (bottom-navigation scaffold)
│   └── theme/             Material 3 theme and WeatherPalette (card colours)
├── MainActivity.kt
└── WeatherApplication.kt  Hilt entry point
```

## Running the app

1. Open the project in Android Studio and let Gradle sync (it needs JDK 17 or newer).
2. Run the `app` configuration on an emulator or device running Android 8.0 (API 26) or later.

From the command line:

```
./gradlew assembleDebug        # build the debug APK
./gradlew testDebugUnitTest    # run the unit tests
```

## Tests

There are 14 unit tests in `app/src/test/java/com/jcu/utilityweather/`:

- `WeatherViewModelTest` (5) — initial load, error propagation, re-fetching when a unit
  changes, display toggles not re-fetching, and blank-city handling.
- `WeatherRepositoryImplTest` (6) — mapping geocoding and forecast into one model, requesting the
  chosen units, unknown city, offline, timeout and server-error messages.

- `WeatherCategoryTest` (2) — weather codes map to the right category, unknown codes fall back.
- `WeatherPaletteTest` (1) — every card colour pair in light and dark mode meets 4.5:1 contrast.

The ViewModel and repository tests use MockK to fake their dependencies.

## Data source and credits

Weather and geocoding data come from [Open-Meteo.com](https://open-meteo.com/), used under
[CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). Weather icons are standard emoji.

## Known limitations

- Settings are intentionally not persisted (per the assessment brief). They survive switching
  tabs and rotating the screen because they live in the ViewModel, but reset when the app restarts.
- Only current conditions and today's high/low are shown; there is no multi-day forecast.
- There is no location detection; the user types a city name.

## AI assistance

The code, tests and documentation in this repository were written with the help of an AI
assistant (Claude). This is declared in the assessment's Declaration of AI-Generated Material.

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

**Resolved:** settings are held in `WeatherViewModel`, so they persist while switching between the
main and settings screens and across rotation, and reset only when the app restarts.

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

**Resolved:** high/low stays on the main card; feels-like, humidity and wind moved behind a
"Show details" toggle.

### 2026-10-18 - Rebuilding the app step by step and testing on the emulator

- An earlier version of the whole app had been committed in one go on 2026-09-17 (branch `main`).
  It was rebuilt step by step on branch `rebuild-incremental` (now the default branch): Compose
  skeleton, weather card, settings screen, ViewModel and repository, Hilt, Retrofit, then tests
  and this README.
- Tested on the Android emulator: live data, the details toggle, unit switching, and an unknown city
  all behaved correctly.
- Found while testing: with networking off the UI showed a raw `Unable to resolve host ...`
  exception. Fixed by mapping network failures to friendly `WeatherException` messages, with unit
  tests for each case.

### 2026-10-19 - Small and landscape screens

- Rotated the app on the emulator and found that in landscape the weather card was cut off below the
  search field and the lower settings were unreachable.
- Fixed by making both screens scrollable, capping content width, using a side-by-side layout in
  landscape with a compact card, and splitting `WeatherGlanceCard` into smaller composables.
  Added `@Preview`s for idle, loading, error, dark, landscape and Fahrenheit states.
- Added the missing Gradle wrapper so `./gradlew` works after a fresh clone.
- Tinted the weather card by condition. Checked it on the emulator in light and dark mode with
  different cities: in light mode the whitish cloud and snow emoji blended into very pale
  backgrounds, so those light backgrounds were deepened. A unit test now checks the text contrast
  of every colour pair.
