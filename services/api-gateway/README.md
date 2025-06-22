# API Gateway Service

## Overview
The API Gateway serves as the single entry point for the FinShare application, providing centralized routing, authentication, and cross-cutting concerns management.

## Architecture
- **Technology**: Spring Boot 3 with embedded Tomcat
- **Port**: 5000
- **Role**: Front door, security perimeter, request routing

## Core Responsibilities

### 1. Request Routing
Routes incoming requests to appropriate microservices based on path patterns.

| Path Pattern | Target Service | Purpose |
|--------------|----------------|---------|
| `/api/users/**` | User Service (8001) | User management |
| `/api/groups/**` | Group Expense Service (8002) | Group operations |
| `/api/expenses/**` | Group Expense Service (8002) | Expense management |
| `/api/balances/**` | Balance Settlement Service (8003) | Balance calculations |
| `/api/settlements/**` | Balance Settlement Service (8003) | Settlement operations |
| `/api/ai/**` | AI Service (8004) | ML categorization & Co-Pilot |
| `/api/analytics/**` | Analytics Service (8005) | Insights & reporting |
| `/api/budgets/**` | Analytics Service (8005) | Budget management |

### 2. Authentication & Security
- JWT token validation using Firebase Authentication
- Security filter chain with custom JWT authentication filter
- CORS configuration for cross-origin requests
- Development mode security bypass for testing

### 3. Cross-Cutting Concerns
- Centralized logging and request tracking
- Rate limiting infrastructure
- Error handling and response formatting
- Health check endpoint

## API Examples

### Health Check
```bash
GET http://localhost:5000/health
```
**Response:**
```json
{
  "service": "FinShare API Gateway",
  "version": "1.0.0", 
  "status": "UP",
  "timestamp": "2025-06-22T17:40:03.123Z"
}
```

### Authenticated Request Flow
```bash
# Request with JWT token
curl -X GET http://localhost:5000/api/ai/categories \
  -H "Authorization: Bearer <firebase-jwt-token>"
```

### Proxied AI Service Request
```bash
# Gateway routes to AI Service (8004)
POST http://localhost:5000/api/ai/categorize
Content-Type: application/json

{
  "merchant_text": "Starbucks Coffee",
  "transaction_type": "DEBIT",
  "amount": 15.50
}
```

## Configuration

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // JWT authentication filter
    // CORS configuration
    // Security filter chain
}
```

### Web Controller
```java
@Controller
public class WebController {
    @GetMapping("/")
    public String index() {
        // Serves main application interface
    }
}
```

## Development Features

### Web Interface
Access the development interface at `http://localhost:5000` to:
- View service status dashboard
- Test AI categorization with sample data
- Monitor microservices health
- Interactive API testing

### Authentication Bypass
In development mode, certain endpoints bypass authentication:
- `/health` - Health checks
- `/` - Main interface
- `/app` - Application routes

## Security Architecture

### JWT Flow
1. Client obtains Firebase JWT token
2. Client includes token in `Authorization: Bearer <token>` header
3. Gateway validates token signature and claims
4. Gateway extracts user ID and forwards in `X-Authenticated-User-ID` header
5. Downstream services trust the authenticated user context

### Development Mode
- Firebase credentials not required
- Mock authentication for testing
- Security warnings logged for production readiness

## Monitoring & Health

### Endpoints
- `GET /health` - Service health status
- `GET /actuator/health` - Detailed health information

### Logging
- Request/response logging at DEBUG level
- Authentication warnings for missing tokens
- Service routing information

## Deployment Considerations

### Production Configuration
- Configure Firebase project credentials
- Enable proper JWT validation
- Set up rate limiting rules
- Configure monitoring and alerting

### Scaling
- Stateless design allows horizontal scaling
- Session management via JWT tokens
- Load balancer compatibility

## Error Handling

### Authentication Errors
```json
{
  "error": "Unauthorized",
  "message": "Missing or invalid Authorization header",
  "status": 401
}
```

### Service Unavailable
```json
{
  "error": "Service Unavailable", 
  "message": "Target service temporarily unavailable",
  "status": 503
}
```

## Performance

### Caching Strategy
- JWT public key caching
- Route configuration caching
- Health check response caching

### Connection Pooling
- HTTP client connection pools for downstream services
- Configurable timeouts and retry policies