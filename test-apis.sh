#!/bin/bash

echo "=== FinShare API Test Suite ==="
echo "Testing all endpoints directly"
echo

# Get auth token
echo "1. Testing Authentication..."
TOKEN=$(curl -s http://127.0.0.1:5000/api/auth/login \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@finshare.app","password":"password123"}' | jq -r .token)

if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
    echo "✅ Login successful - Token: ${TOKEN:0:20}..."
else
    echo "❌ Login failed"
    exit 1
fi

echo
echo "2. Testing User Info..."
curl -s http://127.0.0.1:5000/api/auth/user \
  -H "Authorization: Bearer $TOKEN" | jq .

echo
echo "3. Testing Groups API..."
curl -s http://127.0.0.1:5000/api/groups \
  -H "Authorization: Bearer $TOKEN" | jq .

echo
echo "4. Testing Expenses API..."
curl -s http://127.0.0.1:5000/api/expenses \
  -H "Authorization: Bearer $TOKEN" | jq .

echo
echo "5. Testing AI Categorization..."
curl -s http://127.0.0.1:5000/api/ai/categorize \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"description":"Coffee at Starbucks","amount":5.50}' | jq .

echo
echo "6. Testing AI Chat..."
curl -s http://127.0.0.1:5000/api/ai/chat \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"message":"What are good budgeting tips?"}' | jq .

echo
echo "7. Testing Dashboard..."
curl -s http://127.0.0.1:5000/api/dashboard \
  -H "Authorization: Bearer $TOKEN" | jq .

echo
echo "8. Testing Notifications..."
curl -s http://127.0.0.1:5000/api/notifications \
  -H "Authorization: Bearer $TOKEN" | jq .

echo
echo "9. Creating Test Group..."
curl -s http://127.0.0.1:5000/api/groups \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Test Group API","members":["user1"]}' | jq .

echo
echo "10. Creating Test Expense..."
curl -s http://127.0.0.1:5000/api/expenses \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"description":"Test Expense API","amount":25.99,"groupId":"test","category":"Food"}' | jq .

echo
echo "=== Service Health Checks ==="
echo "Auth Backend (5000):"
curl -s http://127.0.0.1:5000/test | jq .

echo
echo "AI Service (8004):"
curl -s http://127.0.0.1:8004/ | jq .

echo
echo "Group Service (8002):"
curl -s http://127.0.0.1:8002/actuator/health | jq .

echo
echo "Balance Service (8003):"
curl -s http://127.0.0.1:8003/actuator/health | jq .

echo
echo "Analytics Service (8005):"
curl -s http://127.0.0.1:8005/actuator/health | jq .

echo
echo "Notification Service (8006):"
curl -s http://127.0.0.1:8006/actuator/health | jq .

echo
echo "=== API Test Complete ==="