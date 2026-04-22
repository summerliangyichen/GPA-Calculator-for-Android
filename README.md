# GPA Calculator for Android

Android GPA calculator app with SHSID-oriented preset support.

## Highlights

- Kotlin Android app
- SHSID grade presets, including IB variants
- Updated launcher icon and Android package naming
- Release-oriented project layout at repository root

## Project Structure

- `app/`: Android application module
- `gradle/`: Gradle wrapper files
- `build.gradle.kts`, `settings.gradle.kts`: Gradle project configuration

## Build

Use the Gradle wrapper from the repository root:

```bash
./gradlew testDebugUnitTest assembleRelease
```

On Windows:

```powershell
.\gradlew.bat testDebugUnitTest assembleRelease
```

## Notes

- `local.properties` is intentionally ignored.
- Build outputs and signed APK artifacts are not committed.
