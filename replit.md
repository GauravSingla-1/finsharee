# FinShare Microservices Application

## Overview

FinShare is a group expense management application built with a microservices architecture. The system enables users to create groups, split expenses using various methods, track balances, and settle debts among group members. The application uses Firebase for authentication and provides AI-powered expense categorization and insights.

## System Architecture

### Microservices Architecture
The application follows a distributed microservices pattern with:
- **API Gateway** (Port 5000) - Single entry point with Firebase JWT authentication
- **User Service** (Port 8001) - User profile and identity management
- **Group Expense Service** (Port 8002) - Core group and expense management
- **Balance Settlement Service** (Port 8003) - Debt calculation and settlement
- **AI Service** (Port 8004) - Expense categorization and insights
- **Analytics Insights Service** (Port 8005) - Dashboard data aggregation and budget management

### Technology Stack
- **Backend**: Java 17 with Spring Boot 3.2.0
- **Authentication**: Firebase Admin SDK with JWT tokens
- **Database**: H2 in-memory database (development), designed for PostgreSQL (production)
- **ORM**: Spring Data JPA with Hibernate
- **Communication**: REST APIs with synchronous service-to-service communication
- **Build Tool**: Maven
- **Additional**: Python FastAPI services for AI functionality

## Key Components

### API Gateway
- **Purpose**: Unified entry point for all client requests
- **Authentication**: Firebase JWT token validation
- **Routing**: Proxies requests to appropriate downstream services
- **Security**: CORS configuration and header forwarding
- **Headers**: Adds `X-Authenticated-User-ID` for downstream authentication

### User Service
- **Purpose**: User profile management and identity resolution
- **Features**: Just-in-time profile creation, phone number search, profile updates
- **Data Model**: User entity with Firebase UID as primary key
- **Database**: H2 with unique constraints on phone numbers

### Group Expense Service
- **Purpose**: Core business logic for groups and expenses
- **Features**: 
  - Group creation and member management
  - Multiple expense split methods (Equal, Exact, Percentage, Shares)
  - Transaction generation for debt tracking
  - Recurring expense support
- **Data Models**: Group, Expense, ExpensePayer, ExpenseSplit, Transaction
- **Split Methods**: Advanced calculation engine for different splitting strategies

### Service Communication
- **Pattern**: Synchronous REST calls between services
- **User Resolution**: Group/Expense service calls User service for member validation
- **Authentication**: Services pass authenticated user ID via headers

## Data Flow

### User Authentication Flow
1. Client sends request with Firebase JWT token
2. API Gateway validates token with Firebase
3. Gateway extracts user ID and forwards as header
4. Downstream services use authenticated user ID for authorization

### Expense Creation Flow
1. User creates expense through API Gateway
2. Group Expense Service validates group membership
3. Service calculates splits based on split method
4. Transactions are generated representing debts between users
5. All operations occur within database transaction for consistency

### Group Management Flow
1. User creates group with optional phone numbers
2. Service validates phone numbers against User Service
3. Only valid users are added as members
4. Creator becomes first member automatically

## External Dependencies

### Firebase
- **Service**: Firebase Admin SDK
- **Purpose**: JWT token validation and user authentication
- **Configuration**: Project ID and service account key
- **Fallback**: Development mode without credentials for testing

### User Service Integration
- **Method**: WebClient for reactive HTTP calls
- **Purpose**: Phone number to user ID resolution
- **Error Handling**: Graceful fallback when users not found

## Deployment Strategy

### Development Environment
- **Runtime**: Replit with Java GraalVM 22.3 and Python 3.11
- **Database**: H2 in-memory for rapid development
- **Build**: Maven with parallel service execution
- **Ports**: Each service runs on dedicated port (5000-8005)

### Service Startup
- **Orchestration**: Replit workflows for parallel service startup
- **Dependencies**: API Gateway waits for downstream services
- **Health Checks**: Each service provides health endpoints

### Configuration Management
- **Files**: YAML configuration with environment variable support
- **Profiles**: Separate test profiles for unit testing
- **Defaults**: Sensible defaults for development environment

## User Preferences

Preferred communication style: Simple, everyday language.

## Changelog

Changelog:
- June 22, 2025. Initial setup
- June 22, 2025. Successfully migrated from Replit Agent to standard Replit environment. Added missing Analytics Insights Service with Redis integration for dashboard data aggregation and budget management. All 6 microservices now running on ports 5000-8005.