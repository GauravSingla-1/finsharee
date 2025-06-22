"""
Expense categorization router for ML-powered category prediction.
"""

import logging
from fastapi import APIRouter, HTTPException, Request, Header
from typing import Optional

from app.models.dtos import CategorizeRequestDto, CategoryResponseDto, AutotrainFeedbackDto

logger = logging.getLogger(__name__)

router = APIRouter()

@router.post("/categorize", response_model=CategoryResponseDto)
async def categorize_expense(
    request: CategorizeRequestDto,
    req: Request,
    user_id: Optional[str] = Header(None, alias="X-Authenticated-User-ID")
):
    """
    Categorize an expense transaction using ML model.
    
    Takes structured transaction data and returns predicted category
    with confidence score and alternative suggestions.
    """
    
    ml_categorizer = req.app.state.ml_categorizer
    
    if not ml_categorizer:
        # Fallback categorization without ML
        return CategoryResponseDto(
            predicted_category="Other",
            confidence_score=0.5,
            alternative_categories=["Shopping", "Food & Dining", "Entertainment"]
        )
    
    try:
        # Get prediction from ML service
        predicted_category, confidence = ml_categorizer.categorize_transaction(
            merchant_text=request.merchant_text,
            transaction_type=request.transaction_type,
            amount=request.amount
        )
        
        # Get alternative suggestions
        alternatives = ml_categorizer.get_alternative_categories(request.merchant_text)
        
        # Remove predicted category from alternatives
        alternatives = [cat for cat in alternatives if cat != predicted_category]
        
        logger.info(f"Categorized '{request.merchant_text}' as '{predicted_category}' with confidence {confidence:.2f}")
        
        return CategoryResponseDto(
            predicted_category=predicted_category,
            confidence_score=confidence,
            alternative_categories=alternatives[:3]  # Top 3 alternatives
        )
        
    except Exception as e:
        logger.error(f"Categorization error: {e}")
        raise HTTPException(status_code=500, detail="Categorization service error")

@router.post("/feedback")
async def record_categorization_feedback(
    feedback: AutotrainFeedbackDto,
    req: Request
):
    """
    Record user feedback for autotraining the categorization model.
    
    This endpoint receives feedback when users correct category predictions,
    which is used to improve the personalized ML model.
    """
    
    ml_categorizer = req.app.state.ml_categorizer
    
    if not ml_categorizer:
        logger.warning("ML categorizer not available for feedback recording")
        return {"status": "acknowledged", "training": False}
    
    try:
        # Add training example for user-specific model improvement
        ml_categorizer.add_training_example(
            user_id=feedback.user_id,
            merchant_text=feedback.merchant_text,
            correct_category=feedback.user_corrected_category
        )
        
        logger.info(f"Recorded feedback: {feedback.merchant_text} -> {feedback.user_corrected_category}")
        
        return {
            "status": "success",
            "message": "Feedback recorded for model improvement",
            "training": True
        }
        
    except Exception as e:
        logger.error(f"Feedback recording error: {e}")
        raise HTTPException(status_code=500, detail="Feedback recording failed")

@router.get("/categories")
async def get_available_categories(req: Request):
    """
    Get list of available expense categories.
    """
    
    ml_categorizer = req.app.state.ml_categorizer
    
    if ml_categorizer:
        categories = ml_categorizer.categories
    else:
        # Default categories
        categories = [
            "Food & Dining",
            "Transportation",
            "Shopping", 
            "Entertainment",
            "Bills & Utilities",
            "Healthcare",
            "Travel",
            "Gas & Fuel",
            "Groceries",
            "Education",
            "Business",
            "Other"
        ]
    
    return {
        "categories": categories,
        "total_count": len(categories)
    }

@router.get("/health")
async def categorization_health(req: Request):
    """Health check for categorization service."""
    
    ml_available = req.app.state.ml_categorizer is not None
    
    return {
        "status": "healthy",
        "ml_categorizer_available": ml_available,
        "service": "expense_categorization"
    }