# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TripPath is an Android application built using Clean Architecture with multi-module structure. The project follows a layered approach with separate modules for domain, data, presentation, and the main app.


- **Package**: `com.tpkits.trippath`
- **Min SDK**: 28, **Target SDK**: 34
- **Build System**: Gradle with Kotlin DSL
- **UI Framework**: Jetpack Compose with Material3
- **Language**: Kotlin with JVM target 11
- **Testing Frameworks**: JUnit for unit tests, Espresso for UI tests
- **Architecture**: Clean architecture with modularization
- **Network**: Uses Ktor for HTTP requests
- **logging**: Uses logger for logging

## Common Commands

### Build Commands
- `./gradlew build` - Assembles and tests the project
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK
- `./gradlew clean` - Clean build directory

### Testing Commands
- `./gradlew test` - Run all unit tests
- `./gradlew testDebugUnitTest` - Run debug unit tests only
- `./gradlew connectedAndroidTest` - Run instrumented tests on connected devices
- `./gradlew connectedDebugAndroidTest` - Run debug instrumented tests

### Code Quality Commands
- `./gradlew lint` - Run lint checks
- `./gradlew lintDebug` - Run lint on debug variant
- `./gradlew lintFix` - Run lint and apply safe fixes
- `./gradlew check` - Run all checks (lint + tests)
### Installation Commands
- `./gradlew installDebug` - Install debug APK on connected device
- `./gradlew uninstallDebug` - Uninstall debug APK

## Architecture

### Project Structure
- **Root module**: Contains Gradle configuration and project-level settings
- **App module** (`/app`): Main application module containing:
    - `MainActivity.kt`: Entry point using Compose with edge-to-edge display
    - UI theme system in `ui/theme/` with Material3 theming
    - Standard Android resource structure (`res/`)
    - Clean architecture with separation of concerns
    - 'data', 'domain', and 'presentation' layers
    - feature based modularization

### Key Configuration Files
- `build.gradle.kts` (root): Project-level Gradle configuration
- `app/build.gradle.kts`: App module configuration with Compose setup
- `gradle/libs.versions.toml`: Version catalog for dependency management
- `settings.gradle.kts`: Gradle settings with repository configuration

### Dependencies
The project uses a version catalog system (`libs.versions.toml`) for dependency management. Key dependencies include:
- AndroidX Core KTX
- Lifecycle Runtime KTX
- Activity Compose
- Compose BOM for UI components
- Material3 for theming
- JUnit and Espresso for testing

### Build Configuration
- Uses Android Gradle Plugin 8.8.0
- Kotlin 2.0.0 with Compose compiler plugin
- ProGuard configuration for release builds (currently disabled)
- Supports both unit tests (JUnit) and instrumented tests (AndroidJUnit + Espresso)
