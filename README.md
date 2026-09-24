# CP3406 Assessment Work

Repository for CP3406/CP5307 assessment items.

## Week 1 setup log
- Installed Android Studio (Quail 4 | 2026.1.4) on macOS (Apple Silicon)
- Completed the Introduction to Kotlin Pathway
- Created this private repository to track assessment work
- Reviewed Assessment 1 (Utility App), Assessment 2 (Ethics Presentation),
  and Assessment 3 (Education App) briefs


## Assessment 1 - Utility App direction notes (Week 1)

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

## Week 2.4 - Compose layout basics practical

- Worked through the Add a Button to an App pathway and the Intro to
  State in Compose codelab (Android Basics with Compose, Unit 2)
- Experiment note: changed the weather glance card's stat `Row` from
  `Arrangement.SpaceBetween` to `Arrangement.SpaceEvenly` and observed
  the stat columns spread out evenly instead of hugging the edges —
  much easier to scan at a glance

### Connecting to the Utility App (Assessment 1)

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
