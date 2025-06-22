# Group Expense Service

## Overview
The Group Expense Service is the core business logic engine of FinShare, handling all group management and expense splitting operations with complex financial calculations.

## Architecture
- **Technology**: Spring Boot 3 with Java 17
- **Port**: 8002
- **Database**: H2 (development), Firestore (production)
- **Role**: Core business logic, group management, expense splitting

## Core Features

### 1. Group Management
Complete CRUD operations for expense groups with member management.

### 2. Expense Management
Advanced expense splitting with multiple algorithms and payment tracking.

### 3. Transaction Management
Atomic financial operations ensuring data consistency and integrity.

### 4. Recurring Expenses
Automated recurring expense generation with flexible scheduling.

## Database Schema

### Groups Table
```sql
CREATE TABLE groups (
    group_id VARCHAR(255) PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    group_image_url VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Expenses Table
```sql
CREATE TABLE expenses (
    expense_id VARCHAR(255) PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    category VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    split_method VARCHAR(255) NOT NULL,
    is_recurring BOOLEAN NOT NULL,
    recurrence_rule VARCHAR(255),
    next_due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Expense Splits Table
```sql
CREATE TABLE expense_splits (
    id VARCHAR(255) PRIMARY KEY,
    expense_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    percentage DECIMAL(5,2),
    shares INTEGER,
    FOREIGN KEY (expense_id) REFERENCES expenses(expense_id)
);
```

## API Examples

### Group Management

#### Create Group
```bash
POST http://localhost:8002/api/groups
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "groupName": "Tokyo Trip 2025",
  "groupImageUrl": "https://example.com/tokyo.jpg",
  "memberPhoneNumbers": ["+1234567890", "+0987654321"]
}
```

**Response (201 Created):**
```json
{
  "groupId": "group-uuid-456",
  "groupName": "Tokyo Trip 2025",
  "groupImageUrl": "https://example.com/tokyo.jpg",
  "memberIds": ["user123", "user456", "user789"],
  "createdBy": "user123",
  "createdAt": "2025-06-22T17:40:00Z"
}
```

#### Get Group Details
```bash
GET http://localhost:8002/api/groups/group-uuid-456
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "groupId": "group-uuid-456",
  "groupName": "Tokyo Trip 2025",
  "groupImageUrl": "https://example.com/tokyo.jpg",
  "memberIds": ["user123", "user456", "user789"],
  "members": [
    {
      "userId": "user123",
      "displayName": "John Doe",
      "profileImageUrl": "https://example.com/john.jpg"
    },
    {
      "userId": "user456", 
      "displayName": "Jane Smith",
      "profileImageUrl": "https://example.com/jane.jpg"
    }
  ],
  "createdBy": "user123",
  "totalExpenses": 1250.00,
  "memberCount": 3
}
```

#### Add Group Member
```bash
POST http://localhost:8002/api/groups/group-uuid-456/members
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "userPhoneNumber": "+1555123456"
}
```

### Expense Management

#### Create Equal Split Expense
```bash
POST http://localhost:8002/api/groups/group-uuid-456/expenses
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "description": "Dinner at Sushi Restaurant",
  "amount": 120.00,
  "category": "Food",
  "paidBy": [
    {
      "userId": "user123",
      "amount": 120.00
    }
  ],
  "split": {
    "method": "EQUAL",
    "details": {}
  },
  "isRecurring": false
}
```

**Response (201 Created):**
```json
{
  "expenseId": "expense-uuid-789",
  "groupId": "group-uuid-456",
  "description": "Dinner at Sushi Restaurant",
  "amount": 120.00,
  "category": "Food",
  "createdBy": "user123",
  "splitMethod": "EQUAL",
  "paidBy": [
    {
      "userId": "user123",
      "amount": 120.00
    }
  ],
  "splits": [
    {
      "userId": "user123",
      "amount": 40.00,
      "percentage": 33.33
    },
    {
      "userId": "user456",
      "amount": 40.00,
      "percentage": 33.33
    },
    {
      "userId": "user789",
      "amount": 40.00,
      "percentage": 33.34
    }
  ],
  "createdAt": "2025-06-22T17:40:00Z"
}
```

#### Create Exact Amount Split
```bash
POST http://localhost:8002/api/groups/group-uuid-456/expenses
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "description": "Hotel Booking",
  "amount": 300.00,
  "category": "Accommodation",
  "paidBy": [
    {
      "userId": "user123",
      "amount": 300.00
    }
  ],
  "split": {
    "method": "EXACT",
    "details": {
      "user123": 150.00,
      "user456": 100.00,
      "user789": 50.00
    }
  }
}
```

#### Create Percentage Split
```bash
POST http://localhost:8002/api/groups/group-uuid-456/expenses
Content-Type: application/json
X-Authenticated-User-ID: user456

{
  "description": "Transportation Costs",
  "amount": 90.00,
  "category": "Transportation",
  "paidBy": [
    {
      "userId": "user456",
      "amount": 90.00
    }
  ],
  "split": {
    "method": "PERCENTAGE",
    "details": {
      "user123": 50.0,
      "user456": 30.0,
      "user789": 20.0
    }
  }
}
```

#### Create Recurring Expense
```bash
POST http://localhost:8002/api/groups/group-uuid-456/expenses
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "description": "Monthly Rent",
  "amount": 1500.00,
  "category": "Bills",
  "paidBy": [
    {
      "userId": "user123",
      "amount": 1500.00
    }
  ],
  "split": {
    "method": "EQUAL",
    "details": {}
  },
  "isRecurring": true,
  "recurrenceRule": "MONTHLY",
  "nextDueDate": "2025-07-01T00:00:00Z"
}
```

### Query Operations

#### Get Group Expenses
```bash
GET http://localhost:8002/api/groups/group-uuid-456/expenses
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "expenses": [
    {
      "expenseId": "expense-uuid-789",
      "description": "Dinner at Sushi Restaurant",
      "amount": 120.00,
      "category": "Food",
      "createdBy": "user123",
      "createdAt": "2025-06-22T17:40:00Z",
      "userOwes": 40.00,
      "userPaid": 120.00,
      "netAmount": 80.00
    }
  ],
  "totalExpenses": 120.00,
  "userTotalOwed": 40.00,
  "userTotalPaid": 120.00
}
```

#### Update Expense
```bash
PUT http://localhost:8002/api/expenses/expense-uuid-789
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "description": "Dinner at Premium Sushi Restaurant",
  "amount": 150.00,
  "category": "Food"
}
```

#### Delete Expense
```bash
DELETE http://localhost:8002/api/expenses/expense-uuid-789
X-Authenticated-User-ID: user123
```

**Response (204 No Content)**

## Splitting Algorithms

### 1. Equal Split
Divides expense equally among all group members.
```java
BigDecimal perPersonAmount = totalAmount.divide(
    BigDecimal.valueOf(memberCount), 
    2, 
    RoundingMode.HALF_UP
);
```

### 2. Exact Amount Split
Users specify exact amounts each person owes.
```java
// Validates that sum of exact amounts equals total
BigDecimal sumOfSplits = exactAmounts.values()
    .stream()
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

### 3. Percentage Split
Users specify percentage each person owes.
```java
BigDecimal userAmount = totalAmount
    .multiply(BigDecimal.valueOf(percentage))
    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
```

### 4. Shares Split
Users specify number of shares each person gets.
```java
BigDecimal perShareAmount = totalAmount.divide(
    BigDecimal.valueOf(totalShares), 
    2, 
    RoundingMode.HALF_UP
);
```

## Transaction Integrity

### Atomic Operations
All expense operations use database transactions:

```java
@Transactional
public ExpenseDto createExpense(String groupId, CreateExpenseDto request) {
    // 1. Create expense record
    // 2. Calculate splits based on method
    // 3. Create expense_splits records
    // 4. Create transaction records for each IOU
    // 5. Publish expense_created event
    // All operations succeed or all rollback
}
```

### Data Consistency
- Foreign key constraints ensure referential integrity
- Validation rules prevent invalid split configurations
- Audit trails track all expense modifications

## Event-Driven Architecture

### Published Events
```java
// After successful expense creation
eventPublisher.publishEvent(new ExpenseCreatedEvent(
    expenseId, groupId, amount, createdBy, memberIds
));

// After expense modification
eventPublisher.publishEvent(new ExpenseUpdatedEvent(
    expenseId, oldAmount, newAmount, modifiedBy
));
```

### Consumed Events
- User profile updates (from User Service)
- Settlement confirmations (from Balance Service)

## Health & Monitoring

### Health Check
```bash
GET http://localhost:8002/actuator/health
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
    }
  }
}
```

### Database Console
Access H2 console at: `http://localhost:8002/h2-console`
- JDBC URL: `jdbc:h2:mem:groupexpensedb`
- Username: `sa`
- Password: (empty)

## Performance Considerations

### Caching Strategy
- Group member lists cached for frequent access
- Expense category mappings preloaded
- User profile data cached from User Service

### Query Optimization
- Indexed foreign keys for fast joins
- Pagination for large expense lists
- Batch processing for recurring expense generation

### Scalability
- Stateless service design
- Database connection pooling
- Async event processing