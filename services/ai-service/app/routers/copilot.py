"""
Co-Pilot router for Gemini-powered intelligent features.
"""

import logging
from fastapi import APIRouter, HTTPException, Request, Header
from typing import Optional

from app.models.dtos import (
    TripBudgetRequestDto, TripBudgetResponseDto, BudgetItem,
    ChatRequestDto, ChatResponseDto
)

logger = logging.getLogger(__name__)

router = APIRouter()

@router.post("/trip-budgeter", response_model=TripBudgetResponseDto)
async def generate_trip_budget(
    request: TripBudgetRequestDto,
    req: Request,
    user_id: Optional[str] = Header(None, alias="X-Authenticated-User-ID")
):
    """
    Generate intelligent trip budget using Gemini AI.
    
    Takes natural language trip description and returns detailed,
    itemized budget with cost estimates.
    """
    
    gemini_service = req.app.state.gemini_service
    
    if not gemini_service:
        # Fallback budget generation
        return _generate_fallback_budget(request)
    
    try:
        # Generate budget using Gemini
        budget_data = await gemini_service.generate_trip_budget(
            prompt_text=request.prompt_text,
            destination=request.destination,
            duration_days=request.duration_days,
            budget_range=request.budget_range
        )
        
        # Convert to response format
        budget_items = [
            BudgetItem(
                category=item["category"],
                estimated_cost=item["estimated_cost"],
                description=item["description"]
            )
            for item in budget_data.get("budget_items", [])
        ]
        
        total_cost = budget_data.get("total_estimated_cost", sum(item.estimated_cost for item in budget_items))
        
        logger.info(f"Generated trip budget for user {user_id}: ${total_cost:.2f}")
        
        return TripBudgetResponseDto(
            budget_items=budget_items,
            total_estimated_cost=total_cost,
            currency=budget_data.get("currency", "USD")
        )
        
    except Exception as e:
        logger.error(f"Trip budget generation error: {e}")
        # Return fallback budget instead of error
        return _generate_fallback_budget(request)

@router.post("/assistant", response_model=ChatResponseDto)
async def chat_with_assistant(
    request: ChatRequestDto,
    req: Request,
    user_id: Optional[str] = Header(None, alias="X-Authenticated-User-ID")
):
    """
    Chat with FinShare Co-Pilot assistant.
    
    Provides personalized financial advice and general assistance
    using privacy-preserving Gemini integration.
    """
    
    gemini_service = req.app.state.gemini_service
    
    if not gemini_service:
        # Fallback response
        return ChatResponseDto(
            reply="I'm here to help with your financial questions! However, my AI capabilities are currently limited. Please try again later."
        )
    
    try:
        # Convert conversation history to expected format
        history = None
        if request.conversation_history:
            history = [
                {"role": msg.role, "text": msg.text}
                for msg in request.conversation_history
            ]
        
        # Get response from Gemini service
        response = await gemini_service.chat_with_copilot(
            message=request.message,
            conversation_history=history,
            user_id=user_id
        )
        
        logger.info(f"Generated Co-Pilot response for user {user_id}")
        
        return ChatResponseDto(
            reply=response,
            category_analysis=request.user_context  # Pass through any spending analysis
        )
        
    except Exception as e:
        logger.error(f"Co-Pilot chat error: {e}")
        return ChatResponseDto(
            reply="I apologize, but I'm having trouble processing your request right now. Please try again in a moment."
        )

@router.get("/capabilities")
async def get_copilot_capabilities(req: Request):
    """
    Get available Co-Pilot capabilities and features.
    """
    
    gemini_available = req.app.state.gemini_service is not None
    
    capabilities = [
        {
            "name": "Trip Budget Planning",
            "description": "Generate detailed travel budgets based on destinations and preferences",
            "endpoint": "/trip-budgeter",
            "available": gemini_available
        },
        {
            "name": "Personal Finance Assistant", 
            "description": "Get personalized spending advice and financial guidance",
            "endpoint": "/assistant",
            "available": gemini_available
        },
        {
            "name": "Spending Analysis",
            "description": "Analyze spending patterns and provide optimization suggestions",
            "endpoint": "/assistant",
            "available": gemini_available
        }
    ]
    
    return {
        "capabilities": capabilities,
        "gemini_integration": gemini_available,
        "privacy_mode": "enabled"  # Always privacy-preserving
    }

def _generate_fallback_budget(request: TripBudgetRequestDto) -> TripBudgetResponseDto:
    """Generate a fallback budget when Gemini is not available."""
    
    duration = request.duration_days or 3
    destination = request.destination or "destination"
    
    # Simple budget calculation based on duration
    base_daily_cost = 120
    
    budget_items = [
        BudgetItem(
            category="Accommodation",
            estimated_cost=duration * 80,
            description=f"Hotel/lodging for {duration} nights in {destination}"
        ),
        BudgetItem(
            category="Food & Dining",
            estimated_cost=duration * 45,
            description="Meals and dining experiences"
        ),
        BudgetItem(
            category="Transportation",
            estimated_cost=150,
            description="Flights and local transportation"
        ),
        BudgetItem(
            category="Entertainment & Activities",
            estimated_cost=duration * 35,
            description="Tours, attractions, and entertainment"
        ),
        BudgetItem(
            category="Shopping & Souvenirs",
            estimated_cost=80,
            description="Shopping and souvenirs"
        ),
        BudgetItem(
            category="Miscellaneous",
            estimated_cost=70,
            description="Tips, emergency fund, and other expenses"
        )
    ]
    
    total_cost = sum(item.estimated_cost for item in budget_items)
    
    return TripBudgetResponseDto(
        budget_items=budget_items,
        total_estimated_cost=total_cost,
        currency="USD"
    )