"""
FinShare AI Service - The Intelligence Layer

This service provides:
- SMS parsing and expense categorization using ML models
- Autotraining feedback loop for personalized category prediction
- Privacy-preserving gateway to Google Gemini for Co-Pilot features
"""

import os
import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Header
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

from app.routers import categorization, copilot
from app.services.ml_service import MLCategorizer
from app.services.gemini_service import GeminiService

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Global services
ml_categorizer = None
gemini_service = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan management - startup and shutdown events."""
    global ml_categorizer, gemini_service
    
    logger.info("Starting FinShare AI Service...")
    
    # Initialize ML categorizer
    try:
        ml_categorizer = MLCategorizer()
        await ml_categorizer.initialize()
        app.state.ml_categorizer = ml_categorizer
        logger.info("ML Categorizer initialized successfully")
    except Exception as e:
        logger.error(f"Failed to initialize ML Categorizer: {e}")
        # Continue without ML for development
        app.state.ml_categorizer = None
    
    # Initialize Gemini service
    try:
        gemini_service = GeminiService()
        app.state.gemini_service = gemini_service
        logger.info("Gemini Service initialized successfully")
    except Exception as e:
        logger.error(f"Failed to initialize Gemini Service: {e}")
        # Continue without Gemini for development
        app.state.gemini_service = None
    
    yield
    
    logger.info("Shutting down FinShare AI Service...")

# Create FastAPI application
app = FastAPI(
    title="FinShare AI Service",
    description="Machine Learning and Generative AI capabilities for FinShare",
    version="1.0.0",
    lifespan=lifespan
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(categorization.router, prefix="/api/ai", tags=["categorization"])
app.include_router(copilot.router, prefix="/api/ai/co-pilot", tags=["copilot"])

@app.get("/")
async def root():
    """Root endpoint for service health check."""
    return {
        "service": "FinShare AI Service",
        "status": "running",
        "version": "1.0.0",
        "capabilities": [
            "expense_categorization",
            "autotraining_feedback",
            "gemini_copilot_gateway"
        ]
    }

@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {
        "status": "healthy",
        "ml_categorizer": app.state.ml_categorizer is not None,
        "gemini_service": app.state.gemini_service is not None
    }

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8004,
        reload=True,
        log_level="info"
    )