# Architecture

Orient Remote follows **MVVM + Repository pattern** with Hilt for dependency injection, on top of
Jetpack Compose for UI. Everything is synchronous-feeling but built on Kotlin Coroutines/StateFlow
under the hood.

```
UI (Compose Screens)
   │  observes StateFlow, calls ViewModel functions
   ▼
ViewModel (per-screen)
   │  calls suspend/sync functions
   ▼
RemoteRepository (interface)  ──implemented by──▶  RemoteRepositoryImpl
   │                                                    │
   │                                     ┌──────────────┴───────────────┐
   ▼                                     ▼                              ▼
PreferencesManager (DataStore)   OrientIrDatabase (offline)     IrTransmitter (interface)
                                                                        │
                                                            ConsumerIrTransmitter (hardware)
```

## Package layout

```
com.orientremote.app
├── OrientRemoteApplication.kt      Hilt entry point
├── MainActivity.kt                 Single Activity, hosts Compose NavHost
├── di/
│   └── AppModule.kt                Hilt bindings (interfaces → implementations)
├── data/
│   ├── model/                      Brand, RemoteButton, IrCode, IrProfile — pure Kotlin, no Android deps
│   ├── local/
│   │   ├── PreferencesManager.kt   DataStore-backed settings & pairing state
│   │   ├── OrientIrDatabase.kt     Offline IR code profiles for Orient
│   │   └── NecIrEncoder.kt         NEC-protocol pattern generator used to build the database
│   └── repository/
│       ├── RemoteRepository.kt     Interface consumed by ViewModels
│       └── RemoteRepositoryImpl.kt Wires PreferencesManager + OrientIrDatabase + IrTransmitter
├── ir/
│   ├── IrTransmitter.kt            Hardware abstraction interface
│   └── ConsumerIrTransmitter.kt    Real ConsumerIrManager-backed implementation
├── ui/
│   ├── theme/                      Color.kt, Type.kt, Theme.kt (dark/light/system)
│   ├── navigation/NavGraph.kt      Routes + nested "onboarding_graph" for shared ViewModel scope
│   ├── components/                 RemoteActionButton, DirectionPad, RemoteIcon
│   ├── screens/onboarding/         Welcome, BrandSelection, CodeSearch + OnboardingViewModel
│   ├── screens/remote/             RemoteScreen + RemoteViewModel (main control surface)
│   ├── screens/settings/           SettingsScreen + SettingsViewModel
│   └── AppRootViewModel.kt         Decides startup destination + active theme
└── util/
    ├── VibrationHelper.kt          Haptic feedback wrapper
    └── Constants.kt                Long-press timing constants
```

## Why this shape

- **`data.model` has zero Android imports.** `IrCode`, `IrProfile`, `Brand`, `RemoteButton` are
  plain Kotlin data classes/enums, so they're trivially unit-testable and reusable if this logic
  ever moves to a KMP module.
- **`IrTransmitter` is an interface**, not a direct `ConsumerIrManager` reference, so
  `RemoteViewModel`/`OnboardingViewModel` can be tested with a fake transmitter instead of a real
  device.
- **`RemoteRepository` is the single seam** between UI and everything else. Screens never touch
  DataStore or `OrientIrDatabase` directly.
- **Onboarding shares one ViewModel instance** across `BrandSelectionScreen` and
  `CodeSearchScreen` via Navigation Compose's nested-graph back-stack-entry scoping — otherwise
  each screen would get its own Hilt-scoped ViewModel and lose the in-progress search state.

## Extending to a new brand

1. Add the brand to the `Brand` enum (`isAvailable = true`).
2. Create a new database object analogous to `OrientIrDatabase` (or extend it to accept a brand
   parameter if the code layout is similar).
3. Add a `when` branch in `RemoteRepositoryImpl.activeProfile` and `candidateProfiles`.

No ViewModel, screen, or navigation code needs to change — this is the "must support additional
devices without major code changes" requirement from the spec.

## Extending to a new device category (AC, fan, projector, etc.)

1. Add a `DeviceCategory` enum (TV, AC, FAN, ...).
2. Give `IrProfile` a `category: DeviceCategory` field.
3. Add a category-specific `RemoteButton` set (a fan doesn't need `CHANNEL_UP`; an AC needs
   `TEMP_UP`/`TEMP_DOWN`/mode buttons) — either widen the existing enum or introduce a sealed
   button-set type per category.
4. Add a new Compose screen for that category's layout; reuse `RemoteActionButton` and
   `RemoteViewModel`'s hold-to-repeat logic as-is.

## Manual test checklist (per the SRS's Testing phase)

Run these on the actual Tecno Camon 40 Pro (or any IR-equipped Android 12+ device) — none of this
can be verified in the Android emulator, which has no IR hardware:

- [ ] Every button on `RemoteScreen` sends a signal and shows no error snackbar (with a real
      paired TV) / shows the "no IR transmitter" message on a non-IR device.
- [ ] Volume +/- and Channel +/- repeat continuously while held (~every 250ms) and stop the
      instant the finger lifts.
- [ ] Rotating the device is a no-op — the app stays in portrait per `screenOrientation="portrait"`.
- [ ] Switching Dark/Light/System in Settings updates the UI immediately and persists after a
      full app restart.
- [ ] Disabling vibration in Settings stops haptics on every button immediately.
- [ ] First launch shows Welcome → Brand Selection → Code Search, in that order, and cannot be
      re-triggered by simply reopening the app once a profile is saved.
- [ ] Code Search: tapping "No response" advances to the next candidate and re-sends Power;
      tapping "Yes" saves the profile and lands on the Remote screen.
- [ ] Exhausting all candidates shows the "no matching code found" screen with Retry/Change Brand.
- [ ] Force-closing and reopening the app after pairing skips onboarding and goes straight to the
      Remote screen with the same paired profile shown in Settings.
- [ ] On a device with no IR hardware, the Remote screen shows the disabled-state message instead
      of a crash, and Settings/onboarding still function without attempting a transmit.
