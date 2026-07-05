# Orient Remote

A fully offline Android universal remote for Orient TVs, built for phones with a built-in IR
blaster (primary target: **Tecno Camon 40 Pro**). No internet, accounts, ads, or tracking —
ever.

---

## ⚠️ Read this first: about the IR code database

`app/src/main/java/com/orientremote/app/data/local/OrientIrDatabase.kt` generates its codes using
the standard **NEC infrared protocol** (the protocol most budget CRT/LED TV chassis use), varying
the *address byte* across ~30 profiles the way real universal-remote databases organize
manufacturer code sets.

**What this means in practice:** the codes are structurally valid NEC frames, but they were not
captured from a real Orient remote (no such licensed database was available to build this app).
The automatic search wizard is fully functional and will cycle through all 30 candidates — some
may already work on your TV since NEC address/command layouts are often shared across OEM
chassis, but you should treat first real-world testing as "verification," not "guaranteed match."

**To upgrade to verified codes:** capture real signals from a working Orient remote (e.g. with an
IR receiver breakout + oscilloscope/logic analyzer, or a universal "learning" remote's diagnostics
mode) and paste the address/command bytes into `OrientIrDatabase.KNOWN_ADDRESSES` /
`BUTTON_COMMANDS`. No other file needs to change.

---

## Requirements

- Android Studio (Koala or newer recommended)
- JDK 17
- A physical Android 12+ device with an IR blaster (the emulator has none) — Settings → About
  Phone rarely lists this; if unsure, check the manufacturer spec sheet for "IR blaster" /
  "infrared sensor."

## Get a built APK without installing anything

This repo includes a GitHub Actions workflow (`.github/workflows/android-ci.yml`) that builds a
debug APK and runs unit tests on every push to `main` (and via manual "Run workflow" dispatch).

1. Push this repo to GitHub.
2. Go to the **Actions** tab → **Android CI** → open the latest run.
3. Download the **orient-remote-debug-apk** artifact from the run summary — that's your
   installable `app-debug.apk`.

No local Android SDK, Gradle, or Java setup required for this path.

## Building locally

> **Note on the Gradle wrapper:** `gradlew` / `gradlew.bat` are included, but
> `gradle/wrapper/gradle-wrapper.jar` (a small binary, normally produced by `gradle wrapper`)
> is not, since this project was assembled without internet access. Generate it once with a
> local Gradle install:
> ```bash
> gradle wrapper --gradle-version 8.7
> ```
> After that, `./gradlew` works normally on its own for good. Alternatively, just open the
> project in Android Studio — it detects the missing jar and offers to regenerate it
> automatically on first sync.

```bash
git clone <this project>
cd OrientRemote
./gradlew assembleDebug
```

Install on a connected device:

```bash
./gradlew installDebug
```

> This sandbox environment does not have the Android SDK or Gradle wrapper JAR available (no
> internet access to download them), so the project has been reviewed line-by-line but not
> compiled here. Opening it in Android Studio will trigger a normal Gradle sync and should build
> cleanly; if you hit a Gradle/AGP/Kotlin version mismatch, bump the versions in
> `build.gradle.kts` to whatever your installed Android Studio recommends.

## Permissions

Only two are requested, both required for core function:

- `TRANSMIT_IR` — send infrared signals
- `VIBRATE` — haptic feedback on button press (disable anytime in Settings)

No internet, location, camera, microphone, or Bluetooth permissions are declared.

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for the full breakdown (MVVM, Repository pattern, Hilt DI,
package layout, and how to add a new brand or device category).

## Testing

Unit tests live in `app/src/test`. Run with:

```bash
./gradlew test
```

They cover the NEC IR encoder and the offline code database's structural integrity. See
`ARCHITECTURE.md` for the full manual test checklist (physical-device behaviors like haptics and
long-press repeat can't be unit tested and must be verified on-device).

## Privacy

Orient Remote collects nothing, phones home to nothing, and works with the network permission
absent entirely. See the in-app Settings → About screen for the same statement shown to users.
