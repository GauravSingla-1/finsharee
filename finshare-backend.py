#!/usr/bin/env python3
"""
FinShare Backend Server with Authentication
Fixed version that works with current environment
"""
from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import uvicorn
from datetime import datetime
import json
import os
import hashlib
import secrets
from typing import Optional

app = FastAPI(title="FinShare Backend", version="1.0.0")
security = HTTPBearer(auto_error=False)

# Configure CORS for mobile app
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# In-memory user storage
users_db = {
    "demo@finshare.app": {
        "password_hash": hashlib.sha256("password123".encode()).hexdigest(),
        "name": "Demo User",
        "user_id": "user1"
    }
}

# In-memory session storage
active_sessions = {}

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return hashlib.sha256(plain_password.encode()).hexdigest() == hashed_password

def create_session_token(user_id: str) -> str:
    token = secrets.token_urlsafe(32)
    active_sessions[token] = {"user_id": user_id, "created_at": datetime.now()}
    return token

def get_current_user(credentials: Optional[HTTPAuthorizationCredentials] = Depends(security)):
    if not credentials:
        return None
    
    token = credentials.credentials
    session = active_sessions.get(token)
    if not session:
        return None
    
    return session["user_id"]

# Mock data
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
    "recent_expenses": expenses_data[:2]
}

@app.get("/")
async def root():
    return {"message": "FinShare Backend API is running", "version": "1.0.0"}

@app.get("/api/health")
async def health():
    return {"status": "ok", "timestamp": datetime.now().isoformat()}

@app.get("/test")
async def test_connection():
    return {"message": "Backend connection successful", "timestamp": datetime.now().isoformat()}

# Authentication endpoints
@app.post("/api/auth/login")
async def login(credentials: dict):
    email = credentials.get("email")
    password = credentials.get("password")
    
    if not email or not password:
        raise HTTPException(status_code=400, detail="Email and password required")
    
    user = users_db.get(email)
    if not user or not verify_password(password, user["password_hash"]):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    
    token = create_session_token(user["user_id"])
    return {
        "token": token,
        "user": {
            "id": user["user_id"],
            "name": user["name"],
            "email": email
        }
    }

@app.post("/api/auth/register")
async def register(user_data: dict):
    email = user_data.get("email")
    password = user_data.get("password")
    name = user_data.get("name", "User")
    
    if not email or not password:
        raise HTTPException(status_code=400, detail="Email and password required")
    
    if email in users_db:
        raise HTTPException(status_code=400, detail="User already exists")
    
    user_id = f"user{len(users_db) + 1}"
    users_db[email] = {
        "password_hash": hashlib.sha256(password.encode()).hexdigest(),
        "name": name,
        "user_id": user_id
    }
    
    token = create_session_token(user_id)
    return {
        "token": token,
        "user": {
            "id": user_id,
            "name": name,
            "email": email
        }
    }

@app.get("/api/auth/user")
async def get_current_user_info(current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Not authenticated")
    
    for email, user_data in users_db.items():
        if user_data["user_id"] == current_user:
            return {
                "id": user_data["user_id"],
                "name": user_data["name"],
                "email": email
            }
    
    raise HTTPException(status_code=404, detail="User not found")

# Protected endpoints
@app.get("/api/groups")
async def get_groups(current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    return {"groups": groups_data, "total": len(groups_data)}

@app.post("/api/groups")
async def create_group(group_data: dict, current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    new_group = {
        "id": f"group{len(groups_data) + 1}",
        "name": group_data.get("name", "New Group"),
        "description": group_data.get("description", ""),
        "members": [current_user] + group_data.get("members", []),
        "created_at": datetime.now().isoformat()
    }
    groups_data.append(new_group)
    return {"group": new_group, "message": "Group created successfully"}

@app.get("/api/expenses")
async def get_expenses(current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    return {"expenses": expenses_data, "total": len(expenses_data)}

@app.post("/api/expenses")
async def create_expense(expense_data: dict, current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    new_expense = {
        "id": f"exp{len(expenses_data) + 1}",
        "group_id": expense_data.get("group_id", "group1"),
        "description": expense_data.get("description", "New Expense"),
        "amount": float(expense_data.get("amount", 0)),
        "category": expense_data.get("category", "OTHER"),
        "payer": current_user,
        "created_at": datetime.now().isoformat()
    }
    expenses_data.append(new_expense)
    return {"expense": new_expense, "message": "Expense created successfully"}

@app.get("/api/dashboard")
async def get_dashboard(current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    return dashboard_data

# AI endpoints with smart categorization
@app.post("/api/ai/categorize")
async def categorize_expense(request: dict, current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    try:
        merchant = request.get("merchant_text", "")
        if not merchant:
            raise HTTPException(status_code=400, detail="merchant_text is required")
        
        # Smart categorization logic
        merchant_lower = merchant.lower()
        
        # High confidence matches
        if any(word in merchant_lower for word in ["restaurant", "cafe", "food", "pizza", "burger", "mcdonald", "starbucks", "subway", "kfc", "domino"]):
            category, confidence = "FOOD", 0.95
        elif any(word in merchant_lower for word in ["uber", "taxi", "lyft", "gas", "fuel", "shell", "chevron", "exxon", "metro", "bus"]):
            category, confidence = "TRANSPORTATION", 0.93
        elif any(word in merchant_lower for word in ["hotel", "airbnb", "booking", "marriott", "hilton", "hyatt", "motel"]):
            category, confidence = "ACCOMMODATION", 0.92
        elif any(word in merchant_lower for word in ["grocery", "supermarket", "walmart", "target", "costco", "safeway", "kroger", "publix"]):
            category, confidence = "GROCERIES", 0.90
        elif any(word in merchant_lower for word in ["amazon", "shop", "store", "mall", "retail", "best buy", "apple store"]):
            category, confidence = "SHOPPING", 0.88
        elif any(word in merchant_lower for word in ["movie", "cinema", "theater", "netflix", "spotify", "game", "entertainment"]):
            category, confidence = "ENTERTAINMENT", 0.87
        elif any(word in merchant_lower for word in ["electric", "utility", "water", "internet", "phone", "cable", "verizon", "att"]):
            category, confidence = "UTILITIES", 0.85
        elif any(word in merchant_lower for word in ["doctor", "hospital", "pharmacy", "medical", "health", "cvs", "walgreens"]):
            category, confidence = "HEALTHCARE", 0.84
        else:
            category, confidence = "OTHER", 0.70
        
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
async def chat_copilot(request: dict, current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    try:
        message = request.get("message", "")
        if not message:
            raise HTTPException(status_code=400, detail="message is required")
        
        # Smart chat responses based on message content
        message_lower = message.lower()
        
        if any(word in message_lower for word in ["budget", "budgeting"]):
            reply = "Smart budgeting tip: Use the 50/30/20 rule - 50% for needs, 30% for wants, and 20% for savings. Track your expenses in FinShare to see where your money goes and identify areas to optimize."
        elif any(word in message_lower for word in ["save", "saving", "money"]):
            reply = "Here are proven money-saving strategies: 1) Cook at home more often 2) Use group buying for bulk purchases 3) Split subscriptions with friends 4) Compare prices before big purchases 5) Set up automatic transfers to savings."
        elif any(word in message_lower for word in ["split", "group", "expense"]):
            reply = "For fair group expense splitting: Use equal split for shared items like group dinners, percentage split based on income for rent, or exact amounts when people order different things. Always keep receipts and track who paid what."
        elif any(word in message_lower for word in ["category", "categorize"]):
            reply = "FinShare automatically categorizes expenses using AI. Categories include Food, Transportation, Accommodation, Groceries, Shopping, Entertainment, Utilities, and Healthcare. You can always edit categories if needed."
        elif any(word in message_lower for word in ["tip", "advice", "help"]):
            reply = "Pro tip: Set spending alerts for each category, review your monthly spending patterns, and use the receipt scanning feature to quickly add expenses. Group expenses work best when everyone contributes regularly."
        elif any(word in message_lower for word in ["debt", "owe", "settlement"]):
            reply = "To manage group debts effectively: 1) Settle up regularly (weekly or monthly) 2) Use apps like FinShare to track balances 3) Set reminders for payments 4) Be transparent about financial situations 5) Consider payment apps for quick transfers."
        else:
            reply = "I'm your FinShare Co-Pilot! I can help with budgeting advice, expense categorization, group splitting strategies, money-saving tips, and debt management. What would you like to know about managing your finances?"
        
        return {"reply": reply}
        
    except Exception as e:
        print(f"Chat error: {e}")
        return {"reply": "I'm here to help with your expense management questions! Ask me about budgeting, saving money, or splitting group expenses."}

@app.get("/api/notifications")
async def get_notifications(current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    notifications = [
        {
            "id": "notif1",
            "title": "New expense added",
            "message": "John added $25.50 for lunch",
            "type": "expense",
            "created_at": "2025-06-22T14:30:00Z"
        },
        {
            "id": "notif2", 
            "title": "Settlement reminder",
            "message": "You owe Sarah $15.25",
            "type": "settlement",
            "created_at": "2025-06-22T10:15:00Z"
        }
    ]
    return {"notifications": notifications}

if __name__ == "__main__":
    print("Starting FinShare Backend Server...")
    print("Features: Authentication, Smart AI, Expense Management")
    print("Demo login: demo@finshare.app / password123")
    print("API available at: http://0.0.0.0:5000")
    uvicorn.run(app, host="0.0.0.0", port=5000)