# GPA Calculator for Android

An Android GPA calculator focused on fast entry, clean GPA breakdowns, and SHSID-oriented preset support.

## Overview

This repository contains the Android edition of GPA Calculator. The project is now organized as a standalone Android app repository with the Gradle project at the repository root.

It includes:

- SHSID-oriented presets for lower grades
- Grade 11 IB variants for `No EE` and `With EE`
- Grade 12 IB support
- Updated Android package naming
- A refreshed launcher icon and release-ready project structure

## Feature Set

- Fast course and grade entry workflow
- GPA calculation with weighted-course support
- Preset-based setup for supported grade groups
- Android test coverage for GPA logic and preset behavior

## Supported Presets

- Grades 6-10
- Grade 11 IB (`No EE`)
- Grade 11 IB (`With EE`)
- Grade 12 IB

## Project Layout

- `app/` Android application module
- `gradle/` Gradle wrapper support files
- `build.gradle.kts` root Gradle configuration
- `settings.gradle.kts` project settings

## Requirements

- Android Studio
- Android SDK
- JDK 17 or newer

## Build

From the repository root:

```powershell
.\gradlew.bat testDebugUnitTest assembleRelease
```

## Release APK

The signed APK is published through the repository Releases page when a release is prepared.

## Local Setup

Create a `local.properties` file if needed:

```properties
sdk.dir=D:\\Android\\SDK
```

`local.properties` is ignored and should not be committed.

## Notes

- Build outputs are ignored
- APK signing artifacts are not committed
- This repository is the Android-focused fork and does not include the legacy iOS project files
