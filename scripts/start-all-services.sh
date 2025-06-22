#!/bin/bash

echo "🚀 Starting FinShare Microservices..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Starting API Gateway...${NC}"
cd services/api-gateway && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=5000" &
API_GATEWAY_PID=$!

echo -e "${YELLOW}Starting User Service...${NC}"
cd ../../services/user-service && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8001" &
USER_SERVICE_PID=$!

echo -e "${YELLOW}Starting Group Expense Service...${NC}"
cd ../group-expense-service && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8002" &
GROUP_SERVICE_PID=$!

echo -e "${YELLOW}Starting Balance Settlement Service...${NC}"
cd ../balance-settlement-service && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8003" &
BALANCE_SERVICE_PID=$!

echo -e "${YELLOW}Starting AI Service...${NC}"
cd ../ai-service && python main.py &
AI_SERVICE_PID=$!

echo -e "${YELLOW}Starting Analytics Service...${NC}"
cd ../analytics-insights-service && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8005" &
ANALYTICS_SERVICE_PID=$!

echo -e "${YELLOW}Starting Notification Service...${NC}"
cd ../notification-service && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8006" &
NOTIFICATION_SERVICE_PID=$!

echo -e "${GREEN}All services started!${NC}"
echo "🌐 Application available at: http://localhost:5000"
echo "🤖 AI Service docs at: http://localhost:8004/docs"
echo ""
echo "Process IDs:"
echo "API Gateway: $API_GATEWAY_PID"
echo "User Service: $USER_SERVICE_PID"
echo "Group Service: $GROUP_SERVICE_PID"
echo "Balance Service: $BALANCE_SERVICE_PID"
echo "AI Service: $AI_SERVICE_PID"
echo "Analytics Service: $ANALYTICS_SERVICE_PID"
echo "Notification Service: $NOTIFICATION_SERVICE_PID"

# Wait for all processes
wait