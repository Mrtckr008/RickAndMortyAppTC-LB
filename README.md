# RickAndMorty

A modern Android application built with **Jetpack Compose**, following **Clean Architecture** and **MVVM** principles.  
The app fetches and displays Rick & Morty characters, supports local gallery items, detail screens, image preview, photo download, pagination, analytics, and local persistence.

## Features

- Character list screen
- Character detail screen
- Photo detail screen with zoom support
- Pagination support
- Sort support (newest / oldest)
- Local gallery photo support
- Download photo to device gallery
- Shared element transitions
- Offline/local persistence with Room
- Firebase Analytics and Crashlytics integration
- Unit tests for core logic

## Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **MVVM**
- **Clean Architecture**
- **Hilt**
- **Retrofit**
- **Room**
- **Paging 3**
- **Coil**
- **Coroutines / Flow**
- **Firebase Analytics**
- **Firebase Crashlytics**
- **MockK**
- **Turbine**
- **JUnit**

---

## How to Run the Project

### Requirements

- **Android Studio** latest stable version recommended
- **JDK 17**
- Android SDK installed
- Minimum SDK: **24**
- Compile SDK: **36**

### Steps

1. Clone or download the project.
2. Open the project in Android Studio.
3. Make sure **JDK 17** is selected for the project.
4. Sync Gradle.
5. Run the app on an emulator or physical device.

### Build Variants

For the most realistic performance and behavior, the project should be tested using a **Release APK**, not only Debug builds.

Notes
	•	This project uses a public API, so no API key setup is required.
	•	Firebase is integrated for Analytics and Crashlytics.

⸻

Architecture

This project follows Clean Architecture with MVVM.

Layers

App / Presentation Layer
	•	Built entirely with Jetpack Compose
	•	Contains screens, UI state, reusable UI components, and ViewModels
	•	ViewModels expose state using StateFlow
	•	UI reacts to state changes in a unidirectional way

Domain Layer
	•	Contains business rules and use cases
	•	Independent from Android framework as much as possible
	•	Defines repository contracts

Data Layer
	•	Handles API calls, local database operations, and repository implementations
	•	Uses:
	•	Retrofit for remote data
	•	Room for local persistence
	•	DAO-based local observation
	•	repository implementations for data coordination

Pattern Summary
	•	UI → observes UiState
	•	ViewModel → coordinates state and user actions
	•	UseCase → contains business logic
	•	Repository → abstracts data sources
	•	Local/Remote Data Sources → provide actual data

⸻

Testing

The project includes unit tests for important business and ViewModel logic.

Used tools:
	•	JUnit
	•	MockK
	•	Turbine
	•	kotlinx-coroutines-test

⸻

Performance Note (IMPORTANT)

Although the app works in Debug mode, Release APK testing is strongly recommended for evaluating actual runtime performance, transition smoothness, and image-related behavior.

⸻

Development Notes

This project was completed within 48 hours.

The main focus during this timeframe was:
	•	architecture quality
	•	code organization
	•	maintainability
	•	testability
	•	production-style structure

If more time were allocated, I would further improve:
	•	UI polish
	•	micro interactions
	•	UI Tests
  • ScreenShot Tests
	•	additional UX details

⸻

Project Structure

app/      -> presentation layer, Compose UI, navigation, ViewModels
data/     -> repository implementations, Room, Retrofit, local/remote handling
domain/   -> use cases, repository interfaces, domain models
