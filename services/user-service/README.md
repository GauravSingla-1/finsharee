# User Service

## Overview
The User Service is the identity foundation of FinShare, managing user profiles, authentication integration, and social graph functionality for the group expense management platform.

## Architecture
- **Technology**: Spring Boot 3 with Java 17
- **Port**: 8001 (currently configuring)
- **Database**: H2 (development), Firestore (production)
- **Role**: User identity, profile management, social discovery

## Core Features

### 1. User Profile Management
Complete CRUD operations for user profiles with Firebase Authentication integration.

### 2. Social Discovery
Phone number-based user search for group invitations and friend discovery.

### 3. Just-in-Time Provisioning
Automatic user profile creation upon first authenticated access.

### 4. Dual Identity Model
Internal user IDs with social phone number identifiers for intuitive user experience.

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id VARCHAR(255) PRIMARY KEY, -- Firebase UID
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    profile_image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    last_active TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_email ON users(email);
```

## API Examples

### User Profile Management

#### Get Current User Profile
```bash
GET http://localhost:8001/api/users/me
X-Authenticated-User-ID: firebase-uid-123
```

**Response:**
```json
{
  "userId": "firebase-uid-123",
  "phoneNumber": "+1234567890",
  "displayName": "John Doe",
  "email": "john.doe@example.com",
  "profileImageUrl": "https://example.com/profiles/john.jpg",
  "createdAt": "2025-06-22T10:00:00Z",
  "lastActive": "2025-06-22T17:40:00Z",
  "groupCount": 5,
  "status": "ACTIVE"
}
```

#### Update User Profile
```bash
PUT http://localhost:8001/api/users/me
Content-Type: application/json
X-Authenticated-User-ID: firebase-uid-123

{
  "displayName": "John Smith",
  "profileImageUrl": "https://example.com/profiles/john-new.jpg"
}
```

**Response (200 OK):**
```json
{
  "userId": "firebase-uid-123",
  "phoneNumber": "+1234567890",
  "displayName": "John Smith",
  "email": "john.doe@example.com",
  "profileImageUrl": "https://example.com/profiles/john-new.jpg",
  "updatedAt": "2025-06-22T17:40:00Z"
}
```

#### Create User Profile (Just-in-Time)
This happens automatically when a new authenticated user accesses the system:

```bash
POST http://localhost:8001/api/users/provision
Content-Type: application/json
X-Authenticated-User-ID: firebase-uid-456

{
  "phoneNumber": "+0987654321",
  "displayName": "Jane Smith",
  "email": "jane.smith@example.com"
}
```

**Response (201 Created):**
```json
{
  "userId": "firebase-uid-456",
  "phoneNumber": "+0987654321",
  "displayName": "Jane Smith",
  "email": "jane.smith@example.com",
  "profileImageUrl": null,
  "createdAt": "2025-06-22T17:40:00Z",
  "status": "ACTIVE"
}
```

### Social Discovery

#### Search Users by Phone Number
```bash
GET http://localhost:8001/api/users/search?phone=%2B1234567890
X-Authenticated-User-ID: firebase-uid-123
```

**Response:**
```json
{
  "results": [
    {
      "userId": "firebase-uid-789",
      "displayName": "Alice Johnson",
      "profileImageUrl": "https://example.com/profiles/alice.jpg",
      "phoneNumber": "+1234567890",
      "mutualGroups": 2
    }
  ],
  "totalResults": 1,
  "searchQuery": "+1234567890"
}
```

#### Bulk User Search
```bash
POST http://localhost:8001/api/users/search/bulk
Content-Type: application/json
X-Authenticated-User-ID: firebase-uid-123

{
  "phoneNumbers": ["+1234567890", "+0987654321", "+1555123456"]
}
```

**Response:**
```json
{
  "results": [
    {
      "phoneNumber": "+1234567890",
      "found": true,
      "user": {
        "userId": "firebase-uid-789",
        "displayName": "Alice Johnson",
        "profileImageUrl": "https://example.com/profiles/alice.jpg"
      }
    },
    {
      "phoneNumber": "+0987654321",
      "found": true,
      "user": {
        "userId": "firebase-uid-456",
        "displayName": "Jane Smith",
        "profileImageUrl": null
      }
    },
    {
      "phoneNumber": "+1555123456",
      "found": false,
      "user": null
    }
  ],
  "totalQueried": 3,
  "foundCount": 2
}
```

### User Relationships

#### Get User's Groups
```bash
GET http://localhost:8001/api/users/me/groups
X-Authenticated-User-ID: firebase-uid-123
```

**Response:**
```json
{
  "groups": [
    {
      "groupId": "group-456",
      "groupName": "Tokyo Trip 2025",
      "memberCount": 3,
      "role": "ADMIN",
      "joinedAt": "2025-06-20T10:00:00Z",
      "lastActivity": "2025-06-22T15:30:00Z"
    },
    {
      "groupId": "group-789",
      "groupName": "Office Lunch",
      "memberCount": 5,
      "role": "MEMBER",
      "joinedAt": "2025-06-18T14:00:00Z",
      "lastActivity": "2025-06-22T12:15:00Z"
    }
  ],
  "totalGroups": 2,
  "adminGroups": 1,
  "memberGroups": 1
}
```

#### Get User's Friends/Contacts
```bash
GET http://localhost:8001/api/users/me/contacts
X-Authenticated-User-ID: firebase-uid-123
```

**Response:**
```json
{
  "contacts": [
    {
      "userId": "firebase-uid-456",
      "displayName": "Jane Smith",
      "profileImageUrl": null,
      "phoneNumber": "+0987654321",
      "sharedGroups": 2,
      "relationshipType": "FREQUENT_CONTACT",
      "lastInteraction": "2025-06-22T16:00:00Z"
    },
    {
      "userId": "firebase-uid-789",
      "displayName": "Alice Johnson", 
      "profileImageUrl": "https://example.com/profiles/alice.jpg",
      "phoneNumber": "+1234567890",
      "sharedGroups": 1,
      "relationshipType": "GROUP_MEMBER",
      "lastInteraction": "2025-06-21T10:30:00Z"
    }
  ],
  "totalContacts": 2
}
```

### User Settings & Preferences

#### Get User Settings
```bash
GET http://localhost:8001/api/users/me/settings
X-Authenticated-User-ID: firebase-uid-123
```

**Response:**
```json
{
  "userId": "firebase-uid-123",
  "settings": {
    "privacy": {
      "phoneNumberSearchable": true,
      "profilePublic": true,
      "allowGroupInvites": true
    },
    "notifications": {
      "emailNotifications": true,
      "pushNotifications": true,
      "smsNotifications": false,
      "weeklyDigest": true
    },
    "preferences": {
      "currency": "USD",
      "timezone": "America/New_York",
      "language": "en",
      "dateFormat": "MM/DD/YYYY"
    }
  },
  "updatedAt": "2025-06-22T17:40:00Z"
}
```

#### Update User Settings
```bash
PUT http://localhost:8001/api/users/me/settings
Content-Type: application/json
X-Authenticated-User-ID: firebase-uid-123

{
  "privacy": {
    "phoneNumberSearchable": false,
    "allowGroupInvites": true
  },
  "preferences": {
    "currency": "EUR",
    "timezone": "Europe/London"
  }
}
```

### Administrative Operations

#### Get User by ID (Internal)
```bash
GET http://localhost:8001/api/users/firebase-uid-456
X-Authenticated-User-ID: firebase-uid-123
X-Internal-Service: group-service
```

**Response:**
```json
{
  "userId": "firebase-uid-456",
  "displayName": "Jane Smith",
  "email": "jane.smith@example.com",
  "profileImageUrl": null,
  "status": "ACTIVE",
  "createdAt": "2025-06-22T17:40:00Z"
}
```

#### Batch Get Users (Internal)
```bash
POST http://localhost:8001/api/users/batch
Content-Type: application/json
X-Internal-Service: group-service

{
  "userIds": ["firebase-uid-123", "firebase-uid-456", "firebase-uid-789"]
}
```

**Response:**
```json
{
  "users": [
    {
      "userId": "firebase-uid-123",
      "displayName": "John Smith",
      "profileImageUrl": "https://example.com/profiles/john-new.jpg"
    },
    {
      "userId": "firebase-uid-456",
      "displayName": "Jane Smith",
      "profileImageUrl": null
    },
    {
      "userId": "firebase-uid-789",
      "displayName": "Alice Johnson",
      "profileImageUrl": "https://example.com/profiles/alice.jpg"
    }
  ],
  "notFound": []
}
```

## Authentication Integration

### Firebase Authentication Flow
```java
@Service
public class UserProvisioningService {
    
    @Transactional
    public UserDto provisionUser(String firebaseUid, CreateUserRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findById(firebaseUid);
        if (existingUser.isPresent()) {
            return userMapper.toDto(existingUser.get());
        }
        
        // Validate phone number uniqueness
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException(request.getPhoneNumber());
        }
        
        // Create new user
        User newUser = User.builder()
            .userId(firebaseUid)
            .phoneNumber(request.getPhoneNumber())
            .displayName(request.getDisplayName())
            .email(request.getEmail())
            .createdAt(Instant.now())
            .status(UserStatus.ACTIVE)
            .build();
            
        User savedUser = userRepository.save(newUser);
        
        // Publish user created event
        eventPublisher.publishEvent(new UserCreatedEvent(savedUser));
        
        return userMapper.toDto(savedUser);
    }
}
```

### Just-in-Time User Creation
```java
@Component
public class UserInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-Authenticated-User-ID");
        
        if (userId != null && !userService.userExists(userId)) {
            // Extract user info from Firebase token (if available)
            FirebaseUserInfo userInfo = firebaseService.getUserInfo(userId);
            
            // Create user profile
            CreateUserRequest createRequest = CreateUserRequest.builder()
                .phoneNumber(userInfo.getPhoneNumber())
                .displayName(userInfo.getDisplayName())
                .email(userInfo.getEmail())
                .build();
                
            userService.provisionUser(userId, createRequest);
        }
        
        return true;
    }
}
```

## Social Graph Management

### Phone Number Resolution
```java
@Service
public class PhoneNumberResolverService {
    
    public Optional<UserDto> resolvePhoneNumber(String phoneNumber) {
        // Normalize phone number format
        String normalizedPhone = phoneNumberUtil.normalize(phoneNumber);
        
        // Search in database
        return userRepository.findByPhoneNumber(normalizedPhone)
            .map(userMapper::toDto);
    }
    
    public List<UserSearchResult> bulkResolvePhoneNumbers(List<String> phoneNumbers) {
        return phoneNumbers.stream()
            .map(phone -> {
                Optional<UserDto> user = resolvePhoneNumber(phone);
                return UserSearchResult.builder()
                    .phoneNumber(phone)
                    .found(user.isPresent())
                    .user(user.orElse(null))
                    .build();
            })
            .collect(Collectors.toList());
    }
}
```

### Contact Relationship Analysis
```java
@Service
public class ContactAnalysisService {
    
    public List<ContactDto> analyzeUserContacts(String userId) {
        // Get user's group memberships
        List<String> userGroups = groupService.getUserGroups(userId);
        
        // Find frequent contacts through shared groups
        Map<String, ContactAnalysis> contactFrequency = new HashMap<>();
        
        for (String groupId : userGroups) {
            List<String> groupMembers = groupService.getGroupMembers(groupId);
            groupMembers.stream()
                .filter(memberId -> !memberId.equals(userId))
                .forEach(memberId -> {
                    contactFrequency.computeIfAbsent(memberId, k -> new ContactAnalysis())
                        .addSharedGroup(groupId);
                });
        }
        
        // Convert to contact DTOs with relationship analysis
        return contactFrequency.entrySet().stream()
            .map(entry -> buildContactDto(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(ContactDto::getLastInteraction).reversed())
            .collect(Collectors.toList());
    }
}
```

## Data Privacy & Security

### Phone Number Privacy
```java
@Service
public class PhoneNumberPrivacyService {
    
    public boolean isPhoneNumberSearchable(String userId) {
        UserSettings settings = userSettingsService.getSettings(userId);
        return settings.getPrivacy().isPhoneNumberSearchable();
    }
    
    public UserSearchResult searchWithPrivacyRespect(String phoneNumber, String searcherId) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        
        if (user.isPresent() && isPhoneNumberSearchable(user.get().getUserId())) {
            return UserSearchResult.builder()
                .phoneNumber(phoneNumber)
                .found(true)
                .user(userMapper.toSearchDto(user.get()))
                .build();
        }
        
        return UserSearchResult.builder()
            .phoneNumber(phoneNumber)
            .found(false)
            .build();
    }
}
```

### Data Access Control
```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
public UserDto getUserProfile(String userId) {
    // Method-level security ensures users can only access their own profile
    // unless they have admin role
}

@PreAuthorize("@userService.areInSameGroup(#userId, authentication.name)")
public UserDto getUserForGroupContext(String userId) {
    // Users can only view profiles of people in their groups
}
```

## Performance Optimizations

### Caching Strategy
```java
@Cacheable(value = "user-profiles", key = "#userId")
public UserDto getUserProfile(String userId) {
    // Cache user profiles for 15 minutes
}

@Cacheable(value = "phone-number-lookup", key = "#phoneNumber")
public Optional<UserDto> findByPhoneNumber(String phoneNumber) {
    // Cache phone number lookups for 5 minutes
}

@CacheEvict(value = {"user-profiles", "phone-number-lookup"}, key = "#userId")
public void invalidateUserCache(String userId) {
    // Invalidate cache when user profile is updated
}
```

### Database Optimization
- Indexed phone numbers for fast lookup
- Composite indexes on frequently queried combinations
- Connection pooling for high-throughput operations

## Event-Driven Integration

### Published Events
```java
// User lifecycle events
@EventPublisher
public class UserEventPublisher {
    
    public void publishUserCreated(User user) {
        UserCreatedEvent event = UserCreatedEvent.builder()
            .userId(user.getUserId())
            .phoneNumber(user.getPhoneNumber())
            .displayName(user.getDisplayName())
            .createdAt(user.getCreatedAt())
            .build();
            
        eventPublisher.publishEvent(event);
    }
    
    public void publishUserUpdated(User user, String[] changedFields) {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
            .userId(user.getUserId())
            .changedFields(changedFields)
            .updatedAt(user.getUpdatedAt())
            .build();
            
        eventPublisher.publishEvent(event);
    }
}
```

### Consumed Events
```java
@EventListener
public void handleGroupMemberAdded(GroupMemberAddedEvent event) {
    // Update user's group count and relationships
    userAnalyticsService.updateUserGroupStats(event.getUserId());
}

@EventListener  
public void handleExpenseCreated(ExpenseCreatedEvent event) {
    // Update last activity timestamp for involved users
    List<String> involvedUsers = event.getInvolvedUserIds();
    userActivityService.updateLastActivity(involvedUsers);
}
```

## Monitoring & Health

### Health Check
```bash
GET http://localhost:8001/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "firebase": {
      "status": "UP",
      "details": {
        "connection": "authenticated",
        "mode": "development"
      }
    }
  }
}
```

### User Metrics
- User registration and activation rates
- Phone number search frequency
- Profile completion rates
- User engagement and retention metrics