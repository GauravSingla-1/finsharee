# FinShare Android Application

A modern Android application for group expense management built with Clean Architecture, Jetpack Compose, and Material Design 3.

## Architecture Overview

This application follows Clean Architecture principles with MVVM pattern:

- **Domain Layer**: Business logic, entities, and repository interfaces
- **Data Layer**: API services, DTOs, and repository implementations  
- **Presentation Layer**: UI components, screens, and navigation

## Technology Stack

- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Networking**: Retrofit + OkHttp
- **Serialization**: Kotlinx Serialization
- **Build System**: Gradle with Kotlin DSL

## Project Structure

```
app/src/main/java/com/finshare/android/
├── data/
│   ├── remote/
│   │   ├── dto/          # Data Transfer Objects
│   │   └── FinShareApiService.kt
│   └── repository/       # Repository implementations
├── di/                   # Dependency injection modules
├── domain/
│   ├── model/           # Domain entities
│   └── repository/      # Repository interfaces
└── presentation/
    ├── components/      # Reusable UI components
    ├── navigation/      # Navigation setup
    ├── screens/         # Feature screens
    └── theme/          # Material 3 theming
```

## Features

### Core Functionality
- **Dashboard**: Overview of recent expenses and group summaries
- **Groups**: Create and manage expense groups
- **Expenses**: Add, view, and categorize expenses
- **Profile**: User settings and account management

### Technical Features
- Clean Architecture with proper separation of concerns
- Material Design 3 with dynamic theming
- Bottom navigation with state management
- Dependency injection with Hilt
- Type-safe navigation with Navigation Compose
- Error handling and loading states

## API Integration

The app integrates with the FinShare backend services:

- **API Gateway**: Port 5000 - Main entry point
- **Group Expense Service**: Port 8002 - Group and expense management
- **Balance Settlement Service**: Port 8003 - Transaction calculations
- **AI Service**: Port 8004 - Expense categorization
- **Analytics Service**: Port 8005 - Spending insights
- **Notification Service**: Port 8006 - Push notifications

## Development Setup

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or higher
- Android SDK API 34

### Build Configuration
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Dependencies
- Jetpack Compose BOM 2023.10.01
- Hilt 2.48
- Navigation Compose 2.7.5
- Retrofit 2.9.0
- Kotlinx Serialization 1.6.0

## Building the App

1. Clone the repository
2. Open `android-app` folder in Android Studio
3. Sync Gradle dependencies
4. Run the app on an emulator or device

```bash
cd android-app
./gradlew assembleDebug
```

## Design System

The app uses Material Design 3 with:
- Dynamic color theming
- Consistent typography scale
- Proper elevation and shadows
- Accessible color contrast ratios

## State Management

- **UI State**: Managed with Compose state
- **Navigation State**: Handled by Navigation Compose
- **Data State**: Repository pattern with dependency injection

## Testing Strategy

- **Unit Tests**: Domain layer business logic
- **Integration Tests**: Repository implementations
- **UI Tests**: Compose UI components
- **End-to-End Tests**: Complete user flows

## Security

- JWT token authentication
- Secure API communication over HTTPS
- Input validation and sanitization
- Proper error handling without data leakage

## Performance Optimizations

- Lazy loading for lists
- Image caching and optimization
- Efficient Compose recomposition
- Background processing for API calls

## Future Enhancements

- Offline capability with Room database
- Push notifications integration
- Biometric authentication
- Dark mode support
- Multi-language localization