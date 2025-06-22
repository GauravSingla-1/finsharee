# Analytics Insights Service

## Overview
The Analytics Insights Service processes spending data to generate actionable financial insights, budget management, and personalized recommendations for FinShare users.

## Architecture
- **Technology**: Spring Boot 3 with Java 17
- **Port**: 8005
- **Database**: H2 (development), Firestore (production)
- **Role**: Data analytics, budget management, financial insights

## Core Features

### 1. Budget Management
Comprehensive budget creation, tracking, and monitoring with alerts.

### 2. Spending Analytics
Deep analysis of spending patterns with categorization and trends.

### 3. Financial Insights
AI-powered recommendations and spending optimization suggestions.

### 4. Dashboard Data
Pre-aggregated data for fast dashboard loading and real-time updates.

## Database Schema

### Budgets Table
```sql
CREATE TABLE budgets (
    budget_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    period VARCHAR(255) NOT NULL CHECK (period IN ('MONTHLY', 'WEEKLY')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## API Examples

### Budget Management

#### Create Monthly Budget
```bash
POST http://localhost:8005/api/budgets
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "category": "Food",
  "amount": 500.00,
  "period": "MONTHLY",
  "startDate": "2025-06-01T00:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "budgetId": "budget-456",
  "userId": "user123",
  "category": "Food",
  "amount": 500.00,
  "period": "MONTHLY",
  "startDate": "2025-06-01T00:00:00Z",
  "currentSpent": 0.00,
  "remainingAmount": 500.00,
  "percentageUsed": 0.0,
  "status": "ON_TRACK",
  "createdAt": "2025-06-22T17:40:00Z"
}
```

#### Get User Budgets
```bash
GET http://localhost:8005/api/budgets/user/user123
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "budgets": [
    {
      "budgetId": "budget-456",
      "category": "Food",
      "amount": 500.00,
      "period": "MONTHLY",
      "currentSpent": 320.50,
      "remainingAmount": 179.50,
      "percentageUsed": 64.1,
      "status": "WARNING",
      "daysRemaining": 9,
      "projectedOverspend": 25.00
    },
    {
      "budgetId": "budget-789",
      "category": "Transportation",
      "amount": 200.00,
      "period": "MONTHLY", 
      "currentSpent": 85.00,
      "remainingAmount": 115.00,
      "percentageUsed": 42.5,
      "status": "ON_TRACK",
      "daysRemaining": 9,
      "projectedOverspend": 0.00
    }
  ],
  "totalBudgeted": 700.00,
  "totalSpent": 405.50,
  "overallStatus": "WARNING",
  "monthlyPeriod": "2025-06"
}
```

#### Update Budget
```bash
PUT http://localhost:8005/api/budgets/budget-456
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "amount": 600.00,
  "notes": "Increased for special events this month"
}
```

#### Delete Budget
```bash
DELETE http://localhost:8005/api/budgets/budget-456
X-Authenticated-User-ID: user123
```

### Spending Analytics

#### Get Spending Overview
```bash
GET http://localhost:8005/api/analytics/spending/user123/overview?period=MONTHLY&year=2025&month=6
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "userId": "user123",
  "period": "MONTHLY",
  "year": 2025,
  "month": 6,
  "totalSpent": 1250.75,
  "categoryBreakdown": [
    {
      "category": "Food",
      "amount": 320.50,
      "percentage": 25.6,
      "transactionCount": 15,
      "averageTransaction": 21.37
    },
    {
      "category": "Transportation",
      "amount": 185.25,
      "percentage": 14.8,
      "transactionCount": 8,
      "averageTransaction": 23.16
    },
    {
      "category": "Shopping",
      "amount": 425.00,
      "percentage": 34.0,
      "transactionCount": 6,
      "averageTransaction": 70.83
    }
  ],
  "topMerchants": [
    {
      "merchantName": "Amazon",
      "amount": 245.00,
      "transactionCount": 3
    },
    {
      "merchantName": "Starbucks",
      "amount": 95.50,
      "transactionCount": 12
    }
  ],
  "comparisonToPrevious": {
    "amountChange": 125.25,
    "percentageChange": 11.1,
    "trend": "INCREASING"
  }
}
```

#### Get Category Trends
```bash
GET http://localhost:8005/api/analytics/trends/user123/category/Food?months=6
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "userId": "user123",
  "category": "Food",
  "timeRange": "6_MONTHS",
  "monthlyData": [
    {
      "year": 2025,
      "month": 1,
      "amount": 285.00,
      "transactionCount": 12
    },
    {
      "year": 2025,
      "month": 2,
      "amount": 310.50,
      "transactionCount": 14
    },
    {
      "year": 2025,
      "month": 3,
      "amount": 295.75,
      "transactionCount": 13
    }
  ],
  "averageMonthly": 297.08,
  "trend": "STABLE",
  "seasonality": {
    "highestMonth": 2,
    "lowestMonth": 1,
    "variance": 25.50
  },
  "insights": [
    "Spending is relatively consistent month-to-month",
    "February shows highest food spending, possibly due to dining out during winter",
    "Consider setting a monthly budget of $325 with 10% buffer"
  ]
}
```

### Financial Insights

#### Get Personalized Insights
```bash
GET http://localhost:8005/api/analytics/insights/user123
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "userId": "user123",
  "generatedAt": "2025-06-22T17:40:00Z",
  "insights": [
    {
      "type": "BUDGET_ALERT",
      "priority": "HIGH",
      "title": "Food Budget Alert",
      "message": "You've spent 64% of your monthly food budget with 9 days remaining. At current pace, you may exceed budget by $25.",
      "actionItems": [
        "Consider cooking more meals at home",
        "Set weekly spending limits",
        "Review recent restaurant visits"
      ],
      "category": "Food",
      "impactAmount": 25.00
    },
    {
      "type": "SAVINGS_OPPORTUNITY", 
      "priority": "MEDIUM",
      "title": "Transportation Savings",
      "message": "You could save $45/month by using public transport instead of ride-sharing for short trips.",
      "actionItems": [
        "Use public transport for trips under 2 miles",
        "Consider monthly transit pass",
        "Walk or bike for nearby destinations"
      ],
      "category": "Transportation",
      "impactAmount": 45.00
    },
    {
      "type": "SPENDING_PATTERN",
      "priority": "LOW", 
      "title": "Weekend Spending Spike",
      "message": "Your weekend spending is 40% higher than weekdays, primarily in entertainment and dining.",
      "actionItems": [
        "Set weekend spending budgets",
        "Plan free weekend activities",
        "Pre-plan weekend meals"
      ],
      "category": "Entertainment",
      "impactAmount": 15.00
    }
  ],
  "totalPotentialSavings": 85.00,
  "overallScore": 75,
  "improvementAreas": ["Budget adherence", "Transportation optimization"]
}
```

#### Get Group Spending Insights
```bash
GET http://localhost:8005/api/analytics/group/group-456/insights
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "groupId": "group-456",
  "groupName": "Tokyo Trip",
  "analysisDate": "2025-06-22T17:40:00Z",
  "totalGroupSpending": 2150.00,
  "spendingByMember": [
    {
      "userId": "user123",
      "displayName": "John Doe",
      "totalSpent": 850.00,
      "percentageOfTotal": 39.5,
      "primaryCategories": ["Accommodation", "Transportation"]
    },
    {
      "userId": "user456",
      "displayName": "Jane Smith", 
      "totalSpent": 650.00,
      "percentageOfTotal": 30.2,
      "primaryCategories": ["Food", "Entertainment"]
    }
  ],
  "categoryInsights": [
    {
      "category": "Food",
      "totalAmount": 425.00,
      "averagePerMeal": 35.42,
      "expensiveVenue": "Premium Sushi Restaurant",
      "suggestion": "Consider mix of premium and budget-friendly dining"
    }
  ],
  "costOptimization": [
    {
      "area": "Accommodation",
      "currentSpend": 900.00,
      "optimizedSpend": 750.00,
      "savings": 150.00,
      "suggestion": "Book accommodations 30 days earlier for 15-20% savings"
    }
  ]
}
```

### Dashboard Data

#### Get Dashboard Summary
```bash
GET http://localhost:8005/api/analytics/dashboard/user123
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "userId": "user123",
  "dashboardDate": "2025-06-22T17:40:00Z",
  "quickStats": {
    "monthlySpent": 1250.75,
    "budgetUtilization": 71.2,
    "activeBudgets": 5,
    "alertsCount": 2
  },
  "recentActivity": [
    {
      "date": "2025-06-22",
      "description": "Lunch at Cafe Roma",
      "amount": 25.50,
      "category": "Food",
      "budgetImpact": "+5.1%"
    }
  ],
  "budgetStatus": [
    {
      "category": "Food",
      "budgeted": 500.00,
      "spent": 320.50,
      "remaining": 179.50,
      "status": "WARNING",
      "daysLeft": 9
    }
  ],
  "topInsights": [
    {
      "message": "You're on track to save $85 this month with small changes",
      "priority": "POSITIVE"
    },
    {
      "message": "Food budget needs attention - 64% used with 30% of month remaining",
      "priority": "WARNING"
    }
  ],
  "spendingChart": {
    "labels": ["Week 1", "Week 2", "Week 3", "Week 4"],
    "data": [285.50, 310.25, 335.00, 320.00],
    "trend": "STABLE"
  }
}
```

## Data Processing Pipeline

### Real-time Analytics
```java
@EventListener
public class SpendingAnalyticsProcessor {
    
    @Async
    public void processExpenseCreated(ExpenseCreatedEvent event) {
        // Update real-time spending totals
        updateSpendingTotals(event.getUserId(), event.getCategory(), event.getAmount());
        
        // Check budget thresholds
        checkBudgetAlerts(event.getUserId(), event.getCategory());
        
        // Update category trends
        updateCategoryTrends(event.getUserId(), event.getCategory(), event.getAmount());
        
        // Generate insights if patterns detected
        generateInsightsIfNeeded(event.getUserId());
    }
}
```

### Batch Processing
```java
@Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
public void generateDailyInsights() {
    List<String> activeUsers = userService.getActiveUsers();
    
    for (String userId : activeUsers) {
        // Analyze spending patterns
        SpendingPattern pattern = analyzeSpendingPattern(userId);
        
        // Generate personalized insights
        List<Insight> insights = generateInsights(userId, pattern);
        
        // Store insights for dashboard
        insightRepository.saveInsights(userId, insights);
        
        // Send notifications for important alerts
        notifyImportantAlerts(userId, insights);
    }
}
```

## Machine Learning Integration

### Spending Prediction
```java
public class SpendingPredictor {
    
    public PredictionResult predictMonthlySpending(String userId, String category) {
        // Get historical data
        List<MonthlySpending> history = getSpendingHistory(userId, category, 12);
        
        // Apply time series analysis
        TimeSeriesModel model = buildTimeSeriesModel(history);
        
        // Generate prediction with confidence interval
        return model.predict(1); // Next month
    }
    
    private List<SpendingAlert> detectAnomalies(String userId) {
        // Statistical analysis for unusual spending patterns
        // Machine learning-based anomaly detection
        // Return alerts for review
    }
}
```

### Budget Recommendations
```java
public class BudgetRecommendationEngine {
    
    public BudgetRecommendation recommendBudget(String userId, String category) {
        // Analyze historical spending
        SpendingStats stats = analyzeSpendingHistory(userId, category);
        
        // Consider seasonal variations
        SeasonalityPattern seasonality = detectSeasonality(userId, category);
        
        // Apply machine learning model
        double recommendedAmount = budgetModel.predict(stats, seasonality);
        
        return new BudgetRecommendation(
            category,
            recommendedAmount,
            stats.getConfidenceLevel(),
            generateRecommendationReasoning(stats)
        );
    }
}
```

## Performance Optimizations

### Caching Strategy
```java
@Cacheable(value = "spending-summary", key = "#userId + '-' + #period")
public SpendingSummary getSpendingSummary(String userId, String period) {
    // Expensive aggregation cached for 1 hour
}

@Cacheable(value = "budget-status", key = "#userId")
public List<BudgetStatus> getBudgetStatus(String userId) {
    // Budget calculations cached for 15 minutes
}
```

### Database Optimization
- Aggregated spending tables for fast queries
- Indexed date ranges for time-based analytics
- Materialized views for complex dashboard queries

## Integration Points

### AI Service Integration
```java
@Service
public class AIInsightIntegration {
    
    @Autowired
    private AIServiceClient aiServiceClient;
    
    public List<Insight> enrichInsightsWithAI(String userId, List<Insight> baseInsights) {
        // Get anonymized spending data
        AnonymizedSpendingData data = anonymizeUserData(userId);
        
        // Call AI service for enhanced insights
        AIInsightResponse aiInsights = aiServiceClient.generateInsights(data);
        
        // Merge with base insights
        return mergeInsights(baseInsights, aiInsights);
    }
}
```

## Monitoring & Health

### Health Check
```bash
GET http://localhost:8005/actuator/health
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

### Analytics Metrics
- Data processing latency
- Insight generation success rate
- Budget accuracy predictions
- User engagement with recommendations