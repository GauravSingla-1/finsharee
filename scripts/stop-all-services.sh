#!/bin/bash

echo "🛑 Stopping FinShare Services..."

# Kill Java Spring Boot processes
pkill -f "spring-boot:run"

# Kill Python FastAPI processes  
pkill -f "python main.py"
pkill -f "uvicorn"

# Kill any remaining Java processes on our ports
lsof -ti:5000 | xargs kill -9 2>/dev/null || true
lsof -ti:8001 | xargs kill -9 2>/dev/null || true
lsof -ti:8002 | xargs kill -9 2>/dev/null || true
lsof -ti:8003 | xargs kill -9 2>/dev/null || true
lsof -ti:8004 | xargs kill -9 2>/dev/null || true
lsof -ti:8005 | xargs kill -9 2>/dev/null || true
lsof -ti:8006 | xargs kill -9 2>/dev/null || true

echo "✅ All FinShare services stopped"