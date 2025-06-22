#!/usr/bin/env python3
"""
Simple backend server to make mobile app functional immediately
Provides basic endpoints for groups, expenses, and dashboard data
"""
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from datetime import datetime
import json
import os
from google import genai

# Configure Gemini AI
genai_client = None
if os.getenv("GEMINI_API_KEY"):
    genai_client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

app = FastAPI(title="FinShare Backend", version="1.0.0")

# Configure CORS for mobile app
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mock data for immediate functionality
groups_data = [
    {
        "id": "group1",
        "name": "Weekend Trip",
        "description": "Our weekend getaway expenses",
        "members": ["user1", "user2", "user3"],
        "created_at": "2025-06-22T10:00:00Z"
    },
    {
        "id": "group2", 
        "name": "Roommates",
        "description": "Monthly shared expenses",
        "members": ["user1", "user4"],
        "created_at": "2025-06-20T15:30:00Z"
    }
]

expenses_data = [
    {
        "id": "exp1",
        "group_id": "group1",
        "description": "Hotel booking",
        "amount": 240.00,
        "category": "ACCOMMODATION",
        "payer": "user1",
        "created_at": "2025-06-22T12:00:00Z"
    },
    {
        "id": "exp2",
        "group_id": "group1", 
        "description": "Dinner at restaurant",
        "amount": 85.50,
        "category": "FOOD",
        "payer": "user2",
        "created_at": "2025-06-22T19:30:00Z"
    }
]

dashboard_data = {
    "total_balance": -42.75,
    "monthly_spending": 567.30,
    "active_groups": 2,
    "recent_expenses": expenses_data[:3]
}

@app.get("/")
async def root():
    return {"service": "FinShare Backend", "status": "operational", "timestamp": datetime.now().isoformat()}

@app.get("/health")
async def health():
    return {"service": "FinShare Backend", "version": "1.0.0", "status": "UP", "timestamp": datetime.now().isoformat()}

@app.get("/api/groups")
async def get_groups():
    return {"groups": groups_data, "total": len(groups_data)}

@app.post("/api/groups")
async def create_group(group_data: dict):
    new_group = {
        "id": f"group{len(groups_data) + 1}",
        "name": group_data.get("name", "New Group"),
        "description": group_data.get("description", ""),
        "members": group_data.get("members", ["user1"]),
        "created_at": datetime.now().isoformat()
    }
    groups_data.append(new_group)
    return {"group": new_group, "message": "Group created successfully"}

@app.get("/api/expenses")
async def get_expenses():
    return {"expenses": expenses_data, "total": len(expenses_data)}

@app.post("/api/expenses")
async def create_expense(expense_data: dict):
    new_expense = {
        "id": f"exp{len(expenses_data) + 1}",
        "group_id": expense_data.get("group_id", "group1"),
        "description": expense_data.get("description", "New Expense"),
        "amount": float(expense_data.get("amount", 0)),
        "category": expense_data.get("category", "OTHER"),
        "payer": expense_data.get("payer", "user1"),
        "created_at": datetime.now().isoformat()
    }
    expenses_data.append(new_expense)
    return {"expense": new_expense, "message": "Expense created successfully"}

@app.get("/api/dashboard")
async def get_dashboard():
    return dashboard_data



@app.post("/api/ai/categorize")
async def categorize_expense(request: dict):
    try:
        merchant = request.get("merchant_text", "")
        if not merchant:
            raise HTTPException(status_code=400, detail="merchant_text is required")
        
        # Use Gemini AI for categorization
        model = genai.GenerativeModel('gemini-1.5-flash')
        prompt = f"""
        Categorize this expense merchant/description: "{merchant}"
        
        Choose from these categories: FOOD, TRANSPORTATION, ACCOMMODATION, GROCERIES, ENTERTAINMENT, SHOPPING, UTILITIES, HEALTHCARE, OTHER
        
        Respond with JSON format:
        {{
            "category": "CATEGORY_NAME",
            "confidence": 0.95
        }}
        """
        
        response = model.generate_content(prompt)
        
        # Parse response
        import re
        json_match = re.search(r'\{.*\}', response.text, re.DOTALL)
        if json_match:
            result = json.loads(json_match.group())
            return {
                "predicted_category": result.get("category", "OTHER"),
                "confidence_score": result.get("confidence", 0.8),
                "alternative_categories": ["ENTERTAINMENT", "SHOPPING", "UTILITIES"]
            }
        else:
            # Fallback categorization
            merchant_lower = merchant.lower()
            if any(word in merchant_lower for word in ["restaurant", "cafe", "food", "pizza", "burger"]):
                category = "FOOD"
                confidence = 0.95
            elif any(word in merchant_lower for word in ["uber", "taxi", "gas", "fuel", "transport"]):
                category = "TRANSPORTATION"
                confidence = 0.92
            elif any(word in merchant_lower for word in ["hotel", "airbnb", "booking", "accommodation"]):
                category = "ACCOMMODATION"
                confidence = 0.90
            elif any(word in merchant_lower for word in ["grocery", "supermarket", "walmart", "target"]):
                category = "GROCERIES"
                confidence = 0.88
            else:
                category = "OTHER"
                confidence = 0.75
            
            return {
                "predicted_category": category,
                "confidence_score": confidence,
                "alternative_categories": ["ENTERTAINMENT", "SHOPPING", "UTILITIES"]
            }
    except Exception as e:
        print(f"Categorization error: {e}")
        return {
            "predicted_category": "OTHER",
            "confidence_score": 0.5,
            "alternative_categories": ["FOOD", "TRANSPORTATION", "SHOPPING"]
        }

@app.post("/api/ai/chat")
async def chat_copilot(request: dict):
    try:
        message = request.get("message", "")
        if not message:
            raise HTTPException(status_code=400, detail="message is required")
        
        # Use Gemini AI for chat
        model = genai.GenerativeModel('gemini-1.5-flash')
        prompt = f"""
        You are FinShare Co-Pilot, a helpful financial assistant for expense management.
        
        User message: "{message}"
        
        Provide helpful, concise advice about expense management, budgeting, or group expense splitting.
        Keep responses under 200 words and be practical.
        """
        
        response = model.generate_content(prompt)
        return {"reply": response.text}
        
    except Exception as e:
        print(f"Chat error: {e}")
        return {"reply": "I'm here to help with your expense management questions! Try asking about budgeting tips, expense categorization, or group expense splitting."}
    
    if "budget" in message:
        response = "Based on your spending patterns, I recommend setting a monthly budget of $800. You're currently spending about $567 this month."
    elif "save" in message or "saving" in message:
        response = "Here are 3 ways to save money: 1) Cook at home more often, 2) Use public transport, 3) Set spending alerts for categories like dining out."
    elif "expense" in message:
        response = "I can help you categorize expenses automatically. Just take a photo of your receipt or enter the merchant name!"
    else:
        response = "I'm your FinShare AI assistant! I can help with budgeting, expense categorization, and financial insights. What would you like to know?"
    
    return {"reply": response}

@app.get("/api/notifications")
async def get_notifications():
    notifications = [
        {
            "id": "notif1",
            "title": "New expense added",
            "message": "John added a $45 dinner expense to Weekend Trip",
            "timestamp": datetime.now().isoformat(),
            "read": False
        }
    ]
    return {"notifications": notifications}

if __name__ == "__main__":
    print("Starting FinShare Backend Server...")
    print("API will be available at: http://localhost:5000")
    uvicorn.run(app, host="0.0.0.0", port=5000, log_level="info")