# Weather App 🌤️

Weather is a premium, feature-rich Android Weather application built with modern standards: **Kotlin**, **Jetpack Compose (Material 3)**, **MVVM + Clean Architecture**, **Hilt Dependency Injection**, **Room Local SQLite Persistence**, **Retrofit**, and **Jetpack Preferences DataStore**. 

The app features a custom **glassmorphic design** using a rich indigo-cyan color palette. It integrates location services using Google Play Services Fused Location Provider, resolving geographic coordinates into local city names using Android's Geocoder API, and fetching up-to-date meteorological conditions from the OpenWeatherMap API.

---

## 🏗️ Architecture & Clean Design System

The project is structured under **Clean Architecture** patterns separated into three layers to adhere to the Separation of Concerns, Single Responsibility Principle, and testability.

### 1. Presentation Layer (MVVM & Compose)
* **Jetpack Compose UI**: Dynamic screens built utilizing Material 3 components, incorporating interactive tabs, animations, glassmorphism card layouts, and linear color gradients.
* **Theme System**: Custom palette using curated colors, enabling rich gradient schemes (from deep space indigo to electric-cyan indicators).
* **ViewModels (`AuthViewModel`, `WeatherViewModel`)**: Retain UI state via robust unidirectional data flow (UDF) backed by Kotlin `StateFlow`. Connects directly with Domain UseCases.
* **Dynamic Graphics Mapping (`WeatherConditionIcon`)**: Decides the weather conditions icon dynamically, utilizing a dual-mode sunrise/sunset and time-of-day condition lookup (e.g. shifts to nocturnal representation/moon icons after 6:00 PM).

### 2. Domain Layer (Pure Business Logic)
* **Use Cases**: Encapsulates specific domain operations as single-purpose interactors (`LoginUseCase`, `RegisterUseCase`, `GetWeatherUseCase`, `GetWeatherHistoryUseCase`, etc.).
* **Repositories (Interfaces)**: Defines clear contracts (`AuthRepository`, `WeatherRepository`) to decouple logic execution from data sources.
* **Entities/Models**: Pure Kotlin data objects (`WeatherInfo`, `User`, `WeatherHistoryItem`).

### 3. Data Layer (Sources & Implementations)
* **Remote Source**: Retrofit service interfaces (`WeatherApiService`) mapping OpenWeatherMap REST endpoints. Parses data using Kotlinx Serialization.
* **Local Cache**: Room Database (`AppDatabase`, `UserDao`, `WeatherHistoryDao`) persisting historical listings (chronologically indexed) and user login credentials.
* **Session Manager**: Persistent login state handling using `Jetpack Preferences DataStore` for faster, asynchronous preference loading.
* **Repository Implementations**: Implement domain repository interfaces to coordinate data dispatching and cache updates.

---

## 📂 Package Directory Structure

```
com.snapwork.weatherapp (or com.snapwork.weatherappdemo)
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── UserDao.kt                  # Database operations for authentication
│   │   │   └── WeatherHistoryDao.kt        # Database operations for local caching
│   │   ├── datastore/
│   │   │   └── SessionManager.kt           # Datastore for session states (Jetpack DataStore)
│   │   ├── entity/
│   │   │   ├── UserEntity.kt               # Local user database entity
│   │   │   └── WeatherHistoryEntity.kt     # Local weather logs cache entity
│   │   └── AppDatabase.kt                  # Room database declaration
│   ├── remote/
│   │   ├── model/
│   │   │   └── WeatherDto.kt               # Retrofit responses mapped via Kotlinx Serialization
│   │   └── WeatherApiService.kt            # Retrofit network interface definitions
│   └── repository/
│       ├── AuthRepositoryImpl.kt           # Concrete authentication data dispatcher
│       └── WeatherRepositoryImpl.kt        # Remote fetches and local caching coordination
├── di/
│   ├── DatabaseModule.kt                   # Hilt providers for Room DB & local storage
│   ├── LocationModule.kt                   # Hilt providers for Fused Location Provider client
│   ├── NetworkModule.kt                    # Hilt provider for Retrofit & logging layers
│   └── RepositoryModule.kt                 # Hilt bindings for repository contracts
├── domain/
│   ├── model/
│   │   ├── User.kt                         # Clean domain user model
│   │   ├── WeatherHistoryItem.kt           # Clean domain historical list element
│   │   └── WeatherInfo.kt                  # Clean domain weather details model
│   ├── repository/
│   │   ├── AuthRepository.kt               # Auth repository contract
│   │   └── WeatherRepository.kt             # Weather repository contract
│   └── usecase/
│       ├── GetSessionUseCase.kt            # UseCase: Fetch current active session
│       ├── GetWeatherHistoryUseCase.kt     # UseCase: Query cache logs flow
│       ├── GetWeatherUseCase.kt            # UseCase: Query remote weather API
│       ├── LoginUseCase.kt                 # UseCase: Log in existing credentials
│       ├── LogoutUseCase.kt                # UseCase: Terminate active preferences session
│       ├── RegisterUseCase.kt              # UseCase: Create a validated new user
│       └── SaveWeatherHistoryUseCase.kt    # UseCase: Log weather into database
├── presentation/
│   ├── auth/
│   │   ├── AuthViewModel.kt                # ViewModel driving registration and login
│   │   ├── LoginScreen.kt                  # UI: Material 3 Sign In
│   │   └── RegistrationScreen.kt            # UI: Material 3 Sign Up
│   ├── components/
│   │   └── WeatherConditionIcon.kt         # Custom graphics mapping and gradient container
│   ├── navigation/
│   │   └── NavGraph.kt                     # Compose screens navigation graph
│   ├── weather/
│   │   ├── HomeScreen.kt                   # Main dashboard, tab layout, weather, history lists
│   │   └── WeatherViewModel.kt             # ViewModel orchestrating geolocation and weather loads
│   └── UiState.kt                          # Sealed interface indicating state (Loading/Success/Error)
├── ui/
│   └── theme/
│       ├── Color.kt                        # Theme colors mapping
│       ├── Theme.kt                        # Material 3 setup and dynamic styling rules
│       └── Type.kt                         # Typography guidelines
└── utils/
    ├── HashUtils.kt                        # SHA-256 password hashing logic
    └── LocationTracker.kt                  # Location coordinate retrieval using FusedLocation
```

---

## ✨ Features Breakdown

### 🔐 1. Secured Authentication Flow
* **Registration & Login**: Full-fledged signup and sign-in interfaces using client-side field validation rules (e.g. strong password checking and email formats).
* **Cryptographic Safety**: Password strings are hashed using a robust, localized SHA-256 algorithm via `MessageDigest` prior to being committed to the database, ensuring raw passwords are never stored in plain text.
* **Persistent Sessions**: Powered by modern Jetpack Preferences DataStore. Upon logging in successfully, user credentials remain saved locally; logging out clears the context and forces the user back to the login interface.

### ☀️ 2. Glassmorphic Current Weather Dashboard
* **Dynamic Location Tracking**: Integrates Google Play Services FusedLocationProvider. It first tests cached location states for lightning-fast loads, and queries high-accuracy coordinates dynamically with full coroutine cancellation support.
* **Double Geocoder Layer**: Coordinates (Latitude, Longitude) are resolved to physical local names (localities, admin areas, and countries) via Android Geocoder APIs with a custom safety timeout fallback.
* **Weather Graphics**: Conditions (Rain, Clouds, Snow, Thunder, Mist/Fog/Haze, or Clear) map to custom premium gradient backgrounds and custom Vector graphics. 
* **Astronomy Details**: Detailed localized times for sunrise and sunset.
* **Nocturnal Visual Logic**: Clear weather displays custom sun elements during the daytime (6:00 AM – 5:59 PM) and automatically transitions to a dark night-stay moon layout starting at 6:00 PM.

### 📂 3. Cache & Offline History Log
* **Room SQL Cache**: All loaded weather metrics are saved automatically to a local SQLite database using Room.
* **History Dashboard**: A dedicated dashboard tab lists previously retrieved listings sorted chronologically. Users can easily view their historical weather checkpoints.

---

## 🛠️ Tech Stack & Version References

All library versions are resolved and managed via Gradle Version Catalogs (`libs.versions.toml`):

| Technology / Component | Library Reference | Version | Description |
|---|---|---|---|
| **Language** | Kotlin | `2.0.0` | Core language and compiler configuration |
| **Dependency Injection** | Dagger Hilt | `2.51.1` | Automated dependency injecting system |
| **Local Cache** | Android Room DB | `2.6.1` | SQLite object mapper and query engine |
| **Session Datastore** | Preferences DataStore | `1.1.1` | Asynchronous keystore settings persistence |
| **Networking** | Retrofit 2 & OkHttp | `2.11.0` / `4.12.0` | HTTP client networking and logging interceptors |
| **Serialization** | Kotlinx Serialization | `1.6.3` | JSON parsing and dto mapping |
| **Location APIs** | Google Play Services Location | `21.3.0` | Geolocation coordinates tracker |
| **Testing Core** | JUnit Jupiter (JUnit 5) | `5.10.2` | Test execution platform and assertions framework |
| **Mocking Framework** | MockK | `1.13.11` | Mocking framework for Kotlin classes |
| **Flow Assertions** | Turbine | `1.1.0` | Testing library for Kotlin Coroutine Flows |
| **Coroutines Test** | kotlinx-coroutines-test | `1.8.1` | Coroutines testing utility suite |

---

## 🛡️ Security Integrations

1. **API Key Isolation**:
   The OpenWeatherMap API key is fetched at build time. It is stored inside `local.properties` and declared inside `build.gradle.kts` as a compile-time custom field:
   ```properties
   OPEN_WEATHER_API_KEY=your_actual_key_here
   ```
   This variable is then injected into `BuildConfig.OPEN_WEATHER_API_KEY`, keeping the key private and preventing developers from accidentally checking it into git.
   
2. **Cleartext Traffic Restriction**:
   The application implements a dedicated network security configuration file (`res/xml/network_security_config.xml`) that restricts cleartext (unencrypted HTTP) requests:
   ```xml
   <network-security-config>
       <base-config cleartextTrafficPermitted="false" />
   </network-security-config>
   ```
   This configuration ensures all network calls are securely dispatched over HTTPS.

---

## 🚀 Setup & Launch Instructions

### Prerequisites
* Android Studio (Koala or newer recommended).
* Android SDK 24+ installed on your developer machine.

### Installation Steps

1. **Clone the Source Code**:
   ```bash
   git clone https://github.com/devendrachavan/weather-app.git
   ```

2. **Configure the Weather API Key**:
   * Visit [OpenWeatherMap](https://openweathermap.org/) and register a free account to obtain an API key.
   * Open the project root folder on your file explorer.
   * Open or create `local.properties` in the root directory.
   * Add the following entry:
     ```properties
     OPEN_WEATHER_API_KEY=your_actual_api_key_here
     ```
     *Note: Replace `your_actual_api_key_here` with your OpenWeatherMap API key.*

3. **Import and Sync**:
   * Open Android Studio.
   * Import the project by navigating to the cloned directory.
   * Let Android Studio compile dependencies and perform the Gradle Sync.

4. **Run the Application**:
   * Configure an Android Emulator (API level 24+) or connect a physical developer device.
   * Enable Location Services/GPS on the running device.
   * Press **Run** in Android Studio.

---

## 🧪 Testing Suite

The project includes unit tests checking all architecture behaviors, use cases, ViewModels, repository logic, and input validation bounds.

### Run Unit Tests
To run the full unit test suite from your terminal, execute the following command:
```bash
.\gradlew.bat testDebugUnitTest --no-daemon --max-workers=2
```
