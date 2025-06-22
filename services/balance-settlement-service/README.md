# Balance Settlement Service

## Overview
The Balance Settlement Service acts as FinShare's financial calculator, computing real-time balances, optimizing debt settlements, and facilitating payments between users.

## Architecture
- **Technology**: Spring Boot 3 with Java 17
- **Port**: 8003
- **Database**: H2 (development), Firestore (production)
- **Role**: Financial calculations, debt optimization, settlement tracking

## Core Features

### 1. Balance Calculation
Real-time computation of who owes whom across groups and overall.

### 2. Debt Simplification
Advanced algorithms to minimize the number of payments needed to settle all debts.

### 3. Settlement Facilitation
Integration with payment apps and manual payment recording.

### 4. Multi-Group Balance Management
Consolidated view of user's financial position across all groups.

## Database Schema

### Transactions Table
```sql
CREATE TABLE transactions (
    transaction_id VARCHAR(255) PRIMARY KEY,
    expense_id VARCHAR(255) NOT NULL,
    group_id VARCHAR(255) NOT NULL,
    from_user_id VARCHAR(255) NOT NULL,
    to_user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    is_settled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    settled_at TIMESTAMP
);
```

## API Examples

### Balance Calculations

#### Get User's Overall Balance
```bash
GET http://localhost:8003/api/balances/user/user123
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "userId": "user123",
  "totalOwed": 245.50,
  "totalOwing": 180.00,
  "netBalance": 65.50,
  "balanceByGroup": [
    {
      "groupId": "group-456",
      "groupName": "Tokyo Trip",
      "amountOwed": 120.00,
      "amountOwing": 80.00,
      "netBalance": 40.00
    },
    {
      "groupId": "group-789",
      "groupName": "Office Lunch",
      "amountOwed": 125.50,
      "amountOwing": 100.00,
      "netBalance": 25.50
    }
  ],
  "lastUpdated": "2025-06-22T17:40:00Z"
}
```

#### Get Group Balance Summary
```bash
GET http://localhost:8003/api/balances/group/group-456
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "groupId": "group-456",
  "groupName": "Tokyo Trip",
  "totalExpenses": 850.00,
  "memberBalances": [
    {
      "userId": "user123",
      "displayName": "John Doe",
      "totalPaid": 450.00,
      "totalOwes": 283.33,
      "netBalance": 166.67,
      "status": "OWED"
    },
    {
      "userId": "user456", 
      "displayName": "Jane Smith",
      "totalPaid": 200.00,
      "totalOwes": 283.33,
      "netBalance": -83.33,
      "status": "OWES"
    },
    {
      "userId": "user789",
      "displayName": "Bob Wilson", 
      "totalPaid": 200.00,
      "totalOwes": 283.34,
      "netBalance": -83.34,
      "status": "OWES"
    }
  ],
  "isBalanced": false,
  "lastUpdated": "2025-06-22T17:40:00Z"
}
```

### Debt Simplification

#### Simplify Group Debts
```bash
POST http://localhost:8003/api/settlements/group/group-456/simplify
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "groupId": "group-456",
  "originalTransactions": 6,
  "optimizedPayments": [
    {
      "fromUserId": "user456",
      "fromUserName": "Jane Smith",
      "toUserId": "user123", 
      "toUserName": "John Doe",
      "amount": 83.33,
      "description": "Settlement for Tokyo Trip expenses"
    },
    {
      "fromUserId": "user789",
      "fromUserName": "Bob Wilson",
      "toUserId": "user123",
      "toUserName": "John Doe", 
      "amount": 83.34,
      "description": "Settlement for Tokyo Trip expenses"
    }
  ],
  "totalPayments": 2,
  "simplificationSavings": 4,
  "totalAmount": 166.67
}
```

#### Get Settlement Suggestions
```bash
GET http://localhost:8003/api/settlements/user/user456/suggestions
X-Authenticated-User-ID: user456
```

**Response:**
```json
{
  "userId": "user456",
  "suggestions": [
    {
      "settlementId": "settlement-123",
      "groupId": "group-456",
      "groupName": "Tokyo Trip",
      "payToUserId": "user123",
      "payToUserName": "John Doe",
      "amount": 83.33,
      "priority": "HIGH",
      "dueDate": "2025-06-29T00:00:00Z",
      "paymentMethods": [
        {
          "method": "VENMO",
          "deepLink": "venmo://paycharge?txn=pay&recipients=john-doe&amount=83.33&note=Tokyo+Trip+Settlement"
        },
        {
          "method": "PAYPAL",
          "deepLink": "https://paypal.me/johndoe/83.33"
        }
      ]
    }
  ],
  "totalOwing": 83.33,
  "numberOfCreditors": 1
}
```

### Settlement Operations

#### Record Manual Payment
```bash
POST http://localhost:8003/api/settlements/record-payment
Content-Type: application/json
X-Authenticated-User-ID: user456

{
  "fromUserId": "user456",
  "toUserId": "user123",
  "amount": 83.33,
  "groupId": "group-456",
  "paymentMethod": "CASH",
  "notes": "Paid in cash after dinner",
  "paymentDate": "2025-06-22T20:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "settlementId": "settlement-456",
  "fromUserId": "user456",
  "toUserId": "user123", 
  "amount": 83.33,
  "groupId": "group-456",
  "paymentMethod": "CASH",
  "notes": "Paid in cash after dinner",
  "status": "COMPLETED",
  "recordedAt": "2025-06-22T17:40:00Z",
  "updatedBalances": {
    "user456": -0.00,
    "user123": 83.34
  }
}
```

#### Confirm Digital Payment
```bash
POST http://localhost:8003/api/settlements/confirm-payment
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "settlementId": "settlement-789",
  "paymentReference": "venmo-txn-abc123",
  "confirmationDate": "2025-06-22T19:30:00Z"
}
```

### Transaction History

#### Get User's Transaction History
```bash
GET http://localhost:8003/api/balances/user/user123/transactions?limit=10&offset=0
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "transactions": [
    {
      "transactionId": "txn-123",
      "expenseId": "expense-456",
      "groupId": "group-456",
      "groupName": "Tokyo Trip",
      "description": "Dinner at Sushi Restaurant",
      "amount": 40.00,
      "type": "OWES",
      "counterpartyUserId": "user123",
      "counterpartyName": "John Doe",
      "isSettled": false,
      "createdAt": "2025-06-22T17:30:00Z"
    },
    {
      "transactionId": "txn-124",
      "expenseId": "expense-789",
      "groupId": "group-456", 
      "groupName": "Tokyo Trip",
      "description": "Hotel Booking",
      "amount": 50.00,
      "type": "OWES",
      "counterpartyUserId": "user123",
      "counterpartyName": "John Doe",
      "isSettled": false,
      "createdAt": "2025-06-22T16:00:00Z"
    }
  ],
  "totalCount": 25,
  "hasMore": true,
  "summary": {
    "totalUnsettled": 180.00,
    "oldestUnsettled": "2025-06-15T10:00:00Z"
  }
}
```

#### Get Group Transaction History
```bash
GET http://localhost:8003/api/balances/group/group-456/transactions
X-Authenticated-User-ID: user123
```

## Debt Simplification Algorithm

### Graph Reduction Approach
The service implements an optimized debt simplification algorithm:

```java
public class DebtSimplificationService {
    
    public List<OptimizedPayment> simplifyDebts(String groupId) {
        // 1. Calculate net balances for each user
        Map<String, BigDecimal> netBalances = calculateNetBalances(groupId);
        
        // 2. Separate creditors (positive balance) and debtors (negative balance)  
        List<UserBalance> creditors = getCreditors(netBalances);
        List<UserBalance> debtors = getDebtors(netBalances);
        
        // 3. Optimize payments using greedy algorithm
        return optimizePayments(creditors, debtors);
    }
    
    private List<OptimizedPayment> optimizePayments(
            List<UserBalance> creditors, 
            List<UserBalance> debtors) {
        
        List<OptimizedPayment> payments = new ArrayList<>();
        
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            UserBalance maxCreditor = getMaxCreditor(creditors);
            UserBalance maxDebtor = getMaxDebtor(debtors);
            
            BigDecimal paymentAmount = maxCreditor.getBalance()
                .min(maxDebtor.getBalance().abs());
            
            payments.add(new OptimizedPayment(
                maxDebtor.getUserId(),
                maxCreditor.getUserId(), 
                paymentAmount
            ));
            
            // Update balances and remove settled users
            updateBalances(maxCreditor, maxDebtor, paymentAmount);
            removeSettledUsers(creditors, debtors);
        }
        
        return payments;
    }
}
```

### Algorithm Complexity
- **Time Complexity**: O(n log n) where n is number of group members
- **Space Complexity**: O(n) for balance storage
- **Optimization**: Reduces payment count from O(n²) to O(n-1)

## Integration Points

### External Payment Apps

#### Venmo Integration
```java
public String generateVenmoDeepLink(String recipientUsername, BigDecimal amount, String note) {
    return String.format(
        "venmo://paycharge?txn=pay&recipients=%s&amount=%.2f&note=%s",
        recipientUsername,
        amount,
        URLEncoder.encode(note, StandardCharsets.UTF_8)
    );
}
```

#### PayPal Integration
```java
public String generatePayPalLink(String recipientEmail, BigDecimal amount) {
    return String.format(
        "https://paypal.me/%s/%.2f",
        recipientEmail.replace("@", ""),
        amount
    );
}
```

### Event Processing

#### Expense Created Event
```java
@EventListener
public void handleExpenseCreated(ExpenseCreatedEvent event) {
    // Create transaction records for each split
    createTransactionsFromExpense(event.getExpenseId());
    
    // Update cached balances
    invalidateBalanceCache(event.getGroupId());
    
    // Send settlement suggestions if threshold reached
    checkSettlementThresholds(event.getGroupId());
}
```

## Performance Optimizations

### Caching Strategy
```java
@Cacheable("user-balances")
public UserBalanceDto getUserBalance(String userId) {
    // Expensive calculation cached for 5 minutes
}

@CacheEvict(value = "group-balances", key = "#groupId")
public void invalidateGroupBalanceCache(String groupId) {
    // Cache invalidation on transaction updates
}
```

### Database Optimization
- Indexed foreign keys for fast joins
- Materialized views for complex balance calculations
- Batch processing for large settlement operations

## Monitoring & Health

### Health Check
```bash
GET http://localhost:8003/actuator/health
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

### Metrics
- Average debt simplification ratio
- Settlement completion rates
- Balance calculation performance
- Payment method usage statistics

## Security Considerations

### Data Privacy
- User balances only visible to group members
- Payment references encrypted in storage
- Audit trail for all financial operations

### Validation
- Amount precision validation (2 decimal places)
- User authorization for balance access
- Group membership verification