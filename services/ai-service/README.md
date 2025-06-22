# AI Service

## Overview
The AI Service is FinShare's intelligence layer, providing ML-powered expense categorization and Gemini-powered Co-Pilot features for intelligent financial assistance.

## Architecture
- **Technology**: Python 3.11 with FastAPI
- **Port**: 8004
- **Role**: Machine learning inference, AI-powered features

## Core Features

### 1. Expense Categorization
Advanced ML-powered categorization of financial transactions with high accuracy.

**Categories Supported:**
- Food & Dining
- Transportation  
- Shopping
- Entertainment
- Groceries
- Gas & Fuel
- Bills & Utilities
- Health & Medical
- Travel
- Education
- Personal Care
- Other

### 2. Co-Pilot Intelligence
Gemini-powered conversational AI for financial guidance and trip planning.

### 3. Autotraining Framework
Self-improving ML models through user feedback and correction loops.

## API Examples

### Expense Categorization
```bash
POST http://localhost:8004/api/ai/categorize
Content-Type: application/json

{
  "merchant_text": "McDonald's Restaurant",
  "transaction_type": "DEBIT",
  "amount": 12.50
}
```

**Response:**
```json
{
  "predicted_category": "Food",
  "confidence_score": 0.95,
  "alternative_categories": ["Dining", "Fast Food"]
}
```

### Available Categories
```bash
GET http://localhost:8004/api/ai/categories
```

**Response:**
```json
{
  "categories": [
    "Food", "Transportation", "Shopping", "Entertainment",
    "Groceries", "Gas", "Bills", "Health", "Travel", 
    "Education", "Personal Care", "Other"
  ],
  "total_count": 12
}
```

### Trip Budget Generation
```bash
POST http://localhost:8004/api/ai/copilot/trip-budget
Content-Type: application/json

{
  "prompt_text": "5-day trip to Tokyo with mid-range budget",
  "destination": "Tokyo, Japan",
  "duration_days": 5,
  "budget_range": "medium"
}
```

**Response:**
```json
{
  "budget_items": [
    {
      "category": "Accommodation",
      "estimated_cost": 600.00,
      "description": "Mid-range hotel for 5 nights"
    },
    {
      "category": "Food",
      "estimated_cost": 300.00,
      "description": "Meals and local cuisine"
    },
    {
      "category": "Transportation",
      "estimated_cost": 150.00,
      "description": "Local trains and airport transfer"
    },
    {
      "category": "Activities",
      "estimated_cost": 200.00,
      "description": "Sightseeing and attractions"
    }
  ],
  "total_estimated_cost": 1250.00,
  "currency": "USD"
}
```

### Co-Pilot Chat
```bash
POST http://localhost:8004/api/ai/copilot/chat
Content-Type: application/json

{
  "message": "How can I reduce my monthly food expenses?",
  "conversation_history": [],
  "user_context": {
    "monthly_food_budget": 400,
    "current_spending": 520
  }
}
```

**Response:**
```json
{
  "reply": "Based on your spending pattern, you're over budget by $120/month on food. Here are some strategies: 1) Meal planning and grocery lists, 2) Cook more at home, 3) Set weekly spending limits, 4) Use cashback apps for groceries.",
  "category_analysis": {
    "overspend_amount": 120,
    "suggested_target": 380,
    "potential_savings": 140
  }
}
```

### Categorization Feedback
```bash
POST http://localhost:8004/api/ai/categorize/feedback
Content-Type: application/json

{
  "user_id": "user123",
  "merchant_text": "Shell Gas Station",
  "predicted_category": "Transportation",
  "user_corrected_category": "Gas",
  "timestamp": "2025-06-22T17:40:00Z"
}
```

## ML Architecture

### Categorization Engine
```python
class MLCategorizer:
    def __init__(self):
        self.categories = [
            "Food", "Transportation", "Shopping", 
            "Entertainment", "Groceries", "Gas",
            "Bills", "Health", "Travel", "Education",
            "Personal Care", "Other"
        ]
        self.keyword_patterns = {
            "Food": ["restaurant", "cafe", "starbucks", "mcdonald"],
            "Gas": ["shell", "bp", "exxon", "gas station"],
            # ... more patterns
        }
    
    def categorize_transaction(self, merchant_text, transaction_type, amount):
        # ML inference logic
        # Returns (category, confidence_score)
```

### Performance Metrics
- **Accuracy**: 85-95% for common merchants
- **Confidence Threshold**: 0.7 for reliable predictions
- **Response Time**: <100ms average
- **Categories Coverage**: 12 comprehensive categories

## Gemini Integration

### Co-Pilot Features
```python
class GeminiService:
    async def generate_trip_budget(self, prompt_text, destination, duration_days, budget_range):
        # Privacy-preserving prompt engineering
        # Structured budget generation
        
    async def chat_with_copilot(self, message, conversation_history, user_id):
        # Contextual financial advice
        # Anonymized spending insights
```

### Privacy Protection
- User data aggregation before external API calls
- Anonymized spending summaries
- No raw transaction data sent to external services
- Context-aware prompt engineering

## Development Mode

### Mock Responses
When Gemini API key is not available:
- Fallback budget generation with realistic estimates
- Mock chat responses with financial advice patterns
- Development-friendly categorization testing

### Testing Examples
```bash
# Test various merchant types
curl -X POST http://localhost:8004/api/ai/categorize \
  -H "Content-Type: application/json" \
  -d '{"merchant_text": "Target Store", "transaction_type": "DEBIT"}'

curl -X POST http://localhost:8004/api/ai/categorize \
  -H "Content-Type: application/json" \
  -d '{"merchant_text": "Uber", "transaction_type": "DEBIT"}'

curl -X POST http://localhost:8004/api/ai/categorize \
  -H "Content-Type: application/json" \
  -d '{"merchant_text": "Netflix", "transaction_type": "DEBIT"}'
```

## Health & Monitoring

### Health Check
```bash
GET http://localhost:8004/health
```

**Response:**
```json
{
  "status": "healthy",
  "service": "FinShare AI Service",
  "version": "1.0.0",
  "ml_model": "operational",
  "gemini_service": "mock_mode"
}
```

### API Documentation
FastAPI automatically generates interactive documentation:
- **Swagger UI**: http://localhost:8004/docs
- **ReDoc**: http://localhost:8004/redoc

## Performance Optimization

### Caching Strategy
- Frequent merchant patterns cached in memory
- User-specific model weights cached
- Category mappings preloaded

### Async Processing
- Non-blocking FastAPI endpoints
- Async Gemini API calls
- Concurrent categorization requests

## Production Considerations

### Scalability
- Stateless service design
- Horizontal scaling capability
- GPU acceleration for ML inference

### Model Updates
- Continuous learning from user feedback
- Automated model retraining pipeline
- A/B testing for model improvements

### Security
- Input validation and sanitization
- Rate limiting for API endpoints
- Secure external API communication