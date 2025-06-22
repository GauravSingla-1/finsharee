#!/usr/bin/env python3
"""
FinShare Backend Server with Authentication and Gemini AI
"""
from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import uvicorn
from datetime import datetime
import json
import os
import google.generativeai as genai
from typing import Optional
import hashlib
import secrets

# Configure Gemini AI
try:
    if os.getenv("GEMINI_API_KEY"):
        genai.configure(api_key=os.getenv("GEMINI_API_KEY"))
        GEMINI_AVAILABLE = True
    else:
        GEMINI_AVAILABLE = False
except Exception as e:
    print(f"Gemini configuration error: {e}")
    GEMINI_AVAILABLE = False

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

# In-memory user storage (replace with database in production)
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
    "recent_expenses": expenses_data[:2]
}

@app.get("/")
async def root():
    return {"message": "FinShare Backend API is running", "version": "1.0.0"}

@app.get("/api/health")
async def health():
    return {"status": "ok", "timestamp": datetime.now().isoformat()}

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
    
    # Find user by ID
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

# AI endpoints with Gemini integration
@app.post("/api/ai/categorize")
async def categorize_expense(request: dict, current_user: Optional[str] = Depends(get_current_user)):
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    try:
        merchant = request.get("merchant_text", "")
        if not merchant:
            raise HTTPException(status_code=400, detail="merchant_text is required")
        
        # Use Gemini AI for categorization if available
        if os.getenv("GEMINI_API_KEY"):
            try:
                model = genai.GenerativeModel('gemini-1.5-flash')
                prompt = f"""
                Categorize this expense merchant/description: "{merchant}"
                
                Choose from these categories: FOOD, TRANSPORTATION, ACCOMMODATION, GROCERIES, ENTERTAINMENT, SHOPPING, UTILITIES, HEALTHCARE, OTHER
                
                Respond with only the category name, nothing else.
                """
                
                response = model.generate_content(prompt)
                category = response.text.strip().upper()
                
                # Validate category
                valid_categories = ["FOOD", "TRANSPORTATION", "ACCOMMODATION", "GROCERIES", "ENTERTAINMENT", "SHOPPING", "UTILITIES", "HEALTHCARE", "OTHER"]
                if category not in valid_categories:
                    category = "OTHER"
                
                return {
                    "predicted_category": category,
                    "confidence_score": 0.95,
                    "alternative_categories": ["ENTERTAINMENT", "SHOPPING", "UTILITIES"]
                }
            except Exception as e:
                print(f"Gemini API error: {e}")
                # Fall through to fallback logic
        
        # Fallback categorization logic
        merchant_lower = merchant.lower()
        if any(word in merchant_lower for word in ["restaurant", "cafe", "food", "pizza", "burger", "mcdonald", "starbucks"]):
            category, confidence = "FOOD", 0.95
        elif any(word in merchant_lower for word in ["uber", "taxi", "gas", "fuel", "transport", "metro", "bus"]):
            category, confidence = "TRANSPORTATION", 0.92
        elif any(word in merchant_lower for word in ["hotel", "airbnb", "booking", "accommodation", "marriott", "hilton"]):
            category, confidence = "ACCOMMODATION", 0.90
        elif any(word in merchant_lower for word in ["grocery", "supermarket", "walmart", "target", "costco", "safeway"]):
            category, confidence = "GROCERIES", 0.88
        elif any(word in merchant_lower for word in ["amazon", "shop", "store", "mall", "retail"]):
            category, confidence = "SHOPPING", 0.85
        else:
            category, confidence = "OTHER", 0.75
        
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
        
        # Use Gemini AI for chat if available
        if os.getenv("GEMINI_API_KEY"):
            try:
                model = genai.GenerativeModel('gemini-1.5-flash')
                prompt = f"""
                You are FinShare Co-Pilot, a helpful financial assistant for expense management and group expense splitting.
                
                User message: "{message}"
                
                Provide helpful, concise advice about:
                - Expense management and budgeting
                - Group expense splitting strategies
                - Financial planning tips
                - Money-saving suggestions
                
                Keep responses under 150 words and be practical and friendly.
                """
                
                response = model.generate_content(prompt)
                return {"reply": response.text}
            except Exception as e:
                print(f"Gemini API error: {e}")
                # Fall through to fallback response
        
        # Fallback responses based on message content
        message_lower = message.lower()
        if "budget" in message_lower:
            reply = "Great question about budgeting! Start by tracking your expenses for a month to see where your money goes. Set realistic spending limits for categories like food, entertainment, and shopping. The 50/30/20 rule is helpful: 50% for needs, 30% for wants, and 20% for savings."
        elif "save" in message_lower or "saving" in message_lower:
            reply = "Here are some quick money-saving tips: Cook at home more often, use group buying for bulk purchases, split subscriptions with friends, and always compare prices before big purchases. Small changes add up to big savings!"
        elif "split" in message_lower or "group" in message_lower:
            reply = "For group expenses, consider these splitting methods: Equal split for shared items like dinner, percentage split based on income for rent, or exact amounts when people order different things. Always keep receipts and use apps like FinShare to track who owes what!"
        else:
            reply = "I'm here to help with your financial questions! Ask me about budgeting tips, saving money, splitting group expenses, or managing your spending. What specific area would you like advice on?"
        
        return {"reply": reply}
        
    except Exception as e:
        print(f"Chat error: {e}")
        return {"reply": "I'm here to help with your expense management questions! Try asking about budgeting tips, expense categorization, or group expense splitting."}

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
    print("Features: Authentication, Gemini AI, Expense Management")
    print("API available at: http://0.0.0.0:8000")
    uvicorn.run(app, host="0.0.0.0", port=8000)