# FinShare Android Development Guide

## Quick Start

### Backend Integration
The Android app connects to the FinShare backend services running on:
- API Gateway: `http://localhost:5000`
- Group Expense Service: `http://localhost:8002`
- Balance Settlement Service: `http://localhost:8003`
- AI Service: `http://localhost:8004`
- Analytics Service: `http://localhost:8005`
- Notification Service: `http://localhost:8006`

### Running the App
1. Ensure backend services are running
2. Open Android Studio
3. Build and run the application
4. The app will connect to backend APIs for live data

## Architecture Implementation

### Clean Architecture Layers

**Domain Layer** (`domain/`)
- `model/`: Core business entities (User, Group, Expense)
- `repository/`: Abstract repository interfaces

**Data Layer** (`data/`)
- `remote/dto/`: API response/request models
- `remote/`: Retrofit API service definitions
- `repository/`: Concrete repository implementations

**Presentation Layer** (`presentation/`)
- `screens/`: Feature-specific UI screens
- `components/`: Reusable UI components
- `navigation/`: App navigation logic
- `theme/`: Material Design 3 theming

### Dependency Injection Structure

**NetworkModule**: Provides Retrofit, OkHttp, and API services
**RepositoryModule**: Binds repository implementations to interfaces
**DatabaseModule**: Future Room database integration

## Screen Implementations

### DashboardScreen
- Displays expense overview and group summaries
- Integration with Analytics Service for spending insights
- Quick actions for common tasks

### GroupsScreen
- Lists user's groups with member counts
- Create new group functionality
- Navigation to group details

### ExpensesScreen
- Recent expenses with categorization
- Add expense with AI-powered categorization
- Expense filtering and search

### ProfileScreen
- User account management
- Settings and preferences
- Security options

## API Integration Patterns

### Repository Pattern
```kotlin
interface GroupRepository {
    suspend fun getUserGroups(): List<Group>
    suspend fun createGroup(name: String, imageUrl: String?): Group
}

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val apiService: FinShareApiService
) : GroupRepository {
    override suspend fun getUserGroups(): List<Group> {
        // API call implementation
    }
}
```

### Error Handling
- Network errors handled gracefully
- User-friendly error messages
- Offline state management
- Retry mechanisms for failed requests

## UI Components

### Material Design 3 Implementation
- Dynamic color theming
- Consistent typography scale
- Proper elevation and shadows
- Accessibility compliance

### Custom Components
- `GroupCard`: Displays group information
- `BottomNavigationBar`: Navigation with state management
- Reusable card layouts and forms

## State Management

### Compose State
- Local state for UI interactions
- Remember state for data persistence
- State hoisting for shared data

### Navigation State
- Bottom navigation with proper state restoration
- Deep linking support
- Argument passing between screens

## Testing Strategy

### Unit Tests
- Repository implementations
- Business logic validation
- Data transformation

### Integration Tests
- API service interactions
- Repository functionality
- End-to-end data flow

### UI Tests
- Screen navigation
- User interactions
- Component behavior

## Performance Optimizations

### Compose Optimizations
- Stable composable functions
- Efficient recomposition
- Lazy loading for lists

### Network Optimizations
- Request caching
- Background processing
- Connection pooling

### Memory Management
- Proper lifecycle handling
- Image loading optimization
- Resource cleanup

## Security Considerations

### Authentication
- JWT token management
- Secure token storage
- Automatic token refresh

### Data Protection
- Input validation
- Secure communication (HTTPS)
- Error message sanitization

## Build Configuration

### Gradle Setup
- Kotlin DSL configuration
- Build variants for different environments
- ProGuard/R8 optimization rules

### CI/CD Integration
- Automated testing
- Code quality checks
- Release automation

## Future Enhancements

### Offline Support
- Room database integration
- Sync mechanism
- Offline-first architecture

### Push Notifications
- Firebase Cloud Messaging
- Real-time expense updates
- Group activity notifications

### Advanced Features
- Biometric authentication
- Dark mode support
- Multi-language localization
- Receipt scanning with OCR