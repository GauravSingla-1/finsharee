# FinShare Folder Structure

## Overview
The FinShare application follows industry-standard microservices folder organization with clear separation of concerns.

## Root Structure

```
finshare/
├── README.md                    # Main project documentation
├── .gitignore                   # Git ignore rules
├── .replit                      # Replit configuration
├── services/                    # All microservices
├── docs/                        # Documentation
├── scripts/                     # Utility scripts
├── shared/                      # Common libraries (future)
└── attached_assets/             # Design documents & assets
```

## Services Structure

Each service follows Spring Boot / FastAPI conventions:

```
services/
├── api-gateway/                 # Main entry point
│   ├── src/main/java/com/finshare/gateway/
│   │   ├── config/             # Security, CORS configuration
│   │   ├── controller/         # Web controllers
│   │   ├── filter/             # JWT authentication
│   │   └── service/            # Business logic
│   └── pom.xml
├── user-service/               # User management
│   ├── src/main/java/com/finshare/user/
│   │   ├── controller/         # User APIs
│   │   ├── service/            # User business logic
│   │   ├── repository/         # Data access
│   │   └── model/              # User entities
│   └── pom.xml
├── group-expense-service/      # Core business logic
│   ├── src/main/java/com/finshare/group/
│   │   ├── controller/         # Group & expense APIs
│   │   ├── service/            # Complex splitting logic
│   │   ├── repository/         # Data persistence
│   │   └── model/              # Group, expense entities
│   └── pom.xml
├── balance-settlement-service/ # Financial calculations
│   ├── src/main/java/com/finshare/balance/
│   │   ├── controller/         # Balance APIs
│   │   ├── service/            # Settlement algorithms
│   │   ├── repository/         # Transaction data
│   │   └── model/              # Transaction entities
│   └── pom.xml
├── ai-service/                 # Python ML service
│   ├── app/
│   │   ├── models/             # Data models (Pydantic)
│   │   ├── routers/            # FastAPI routers
│   │   └── services/           # ML & Gemini services
│   ├── main.py                 # FastAPI entry point
│   └── requirements.txt
├── analytics-insights-service/ # Analytics & budgets
│   ├── src/main/java/com/finshare/analytics/
│   │   ├── controller/         # Analytics APIs
│   │   ├── service/            # Insight generation
│   │   ├── repository/         # Budget data
│   │   └── model/              # Budget entities
│   └── pom.xml
└── notification-service/       # Real-time messaging
    ├── src/main/java/com/finshare/notification/
    │   ├── controller/         # Notification APIs
    │   ├── service/            # Message processing
    │   └── model/              # Message entities
    └── pom.xml
```

## Documentation Structure

```
docs/
├── architecture/               # System design documents
│   ├── FOLDER_STRUCTURE.md    # This file
│   └── architecture_compliance_report.md
├── api/                       # API specifications
│   └── (Future: OpenAPI specs)
└── testing/                   # Test documentation
    └── migration_test_report.md
```

## Scripts Structure

```
scripts/
├── start-all-services.sh      # Start all microservices
├── stop-all-services.sh       # Stop all services
├── test_apis.sh              # API testing script
└── cleanup-structure.sh       # Structure maintenance
```

## Benefits of This Structure

### 1. **Service Independence**
- Each service has its own complete structure
- Independent deployment and scaling
- Clear ownership boundaries

### 2. **Technology Diversity**
- Java services use standard Maven structure
- Python service follows FastAPI conventions
- Each optimized for its specific purpose

### 3. **Development Efficiency**
- Easy navigation and understanding
- Standard IDE integration
- Clear separation of concerns

### 4. **Operational Excellence**
- Simple deployment scripts
- Organized documentation
- Consistent folder patterns

## IDE Integration

### IntelliJ IDEA / VS Code
- Each service can be opened as separate project
- Maven/Python modules auto-detected
- Integrated debugging and testing

### Development Workflow
1. Clone repository
2. Open individual services in IDE
3. Run services independently or via scripts
4. Use organized documentation structure

## Future Enhancements

### Shared Libraries
```
shared/
├── common-dtos/               # Shared data models
├── security-utils/            # Common security utilities
└── testing-framework/        # Shared testing tools
```

### Docker Organization
```
docker/
├── Dockerfile.java           # Java services
├── Dockerfile.python         # Python services
└── docker-compose.yml        # Development environment
```

This structure supports the full development lifecycle from local development to production deployment while maintaining clarity and industry best practices.