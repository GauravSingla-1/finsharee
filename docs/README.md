# FinShare Documentation

## Overview
This directory contains comprehensive documentation for the FinShare microservices application.

## Structure

### Architecture Documentation
- **[Folder Structure](architecture/FOLDER_STRUCTURE.md)** - Complete project organization guide
- **[Architecture Compliance Report](architecture/architecture_compliance_report.md)** - Design document compliance analysis

### Service Documentation
Each service has detailed documentation including:
- API examples with request/response formats
- Database schemas and relationships
- Performance optimizations
- Integration patterns
- Health monitoring

#### Core Services
- **[API Gateway](../services/api-gateway/README.md)** - Single entry point, authentication, routing
- **[User Service](../services/user-service/README.md)** - User profiles, social discovery, authentication
- **[Group Expense Service](../services/group-expense-service/README.md)** - Core business logic, expense splitting
- **[Balance Settlement Service](../services/balance-settlement-service/README.md)** - Financial calculations, debt optimization
- **[AI Service](../services/ai-service/README.md)** - ML categorization, Gemini Co-Pilot features
- **[Analytics Service](../services/analytics-insights-service/README.md)** - Budget management, spending insights
- **[Notification Service](../services/notification-service/README.md)** - Real-time messaging, multi-channel delivery

### Testing Documentation
- **[Migration Test Report](testing/migration_test_report.md)** - Comprehensive testing results
- **[API Testing Script](../scripts/test_apis.sh)** - Automated testing utilities

## Quick Navigation

### Getting Started
1. [Main README](../README.md) - Project overview and quick start
2. [Folder Structure](architecture/FOLDER_STRUCTURE.md) - Understanding the organization
3. [Architecture Compliance](architecture/architecture_compliance_report.md) - Technical implementation status

### Development
1. Start with [API Gateway](../services/api-gateway/README.md) for entry point understanding
2. Review [AI Service](../services/ai-service/README.md) for ML capabilities
3. Explore [Group Expense Service](../services/group-expense-service/README.md) for core business logic

### Operations
1. [Testing Documentation](testing/) - Validation and testing strategies
2. Service-specific health monitoring sections
3. Performance optimization guides

## Documentation Standards

### Service Documentation Format
Each service README follows this structure:
- **Overview** - Purpose and role in the system
- **Architecture** - Technology stack and design
- **Core Features** - Key capabilities
- **API Examples** - Practical usage with real requests/responses
- **Database Schema** - Data model and relationships
- **Performance** - Optimizations and scaling considerations
- **Monitoring** - Health checks and metrics

### Code Examples
- All API examples include complete HTTP requests
- Response examples show realistic data structures
- Code snippets demonstrate actual implementation patterns
- Database schemas include indexes and constraints

### Best Practices
- Examples use realistic business scenarios
- Security considerations included where relevant
- Performance implications documented
- Integration patterns clearly explained

## Contributing to Documentation

### Adding New Documentation
1. Follow the established structure and format
2. Include practical examples with real data
3. Document both success and error scenarios
4. Add cross-references to related services

### Updating Existing Documentation
1. Keep examples current with API changes
2. Update performance metrics as system evolves
3. Maintain consistency across all service docs
4. Verify all links and references work correctly

This documentation serves as the definitive guide for understanding, developing, and operating the FinShare microservices application.