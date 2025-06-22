"""
Data Transfer Objects (DTOs) for the FinShare AI Service.
"""

from typing import List, Optional, Literal
from pydantic import BaseModel, Field

class CategorizeRequestDto(BaseModel):
    """Request DTO for expense categorization."""
    merchant_text: str = Field(..., description="Merchant name or transaction description")
    transaction_type: Literal["DEBIT", "CREDIT"] = Field(..., description="Transaction type")
    amount: Optional[float] = Field(None, description="Transaction amount for context")

class CategoryResponseDto(BaseModel):
    """Response DTO for categorization results."""
    predicted_category: str = Field(..., description="Predicted expense category")
    confidence_score: float = Field(..., description="Confidence score (0.0 to 1.0)")
    alternative_categories: Optional[List[str]] = Field(None, description="Alternative category suggestions")

class TripBudgetRequestDto(BaseModel):
    """Request DTO for trip budget generation."""
    prompt_text: str = Field(..., description="Natural language trip description")
    destination: Optional[str] = Field(None, description="Trip destination")
    duration_days: Optional[int] = Field(None, description="Trip duration in days")
    budget_range: Optional[str] = Field(None, description="Budget range (e.g., 'low', 'medium', 'high')")

class BudgetItem(BaseModel):
    """Individual budget item."""
    category: str = Field(..., description="Expense category")
    estimated_cost: float = Field(..., description="Estimated cost")
    description: str = Field(..., description="Item description")

class TripBudgetResponseDto(BaseModel):
    """Response DTO for trip budget generation."""
    budget_items: List[BudgetItem] = Field(..., description="List of budget items")
    total_estimated_cost: float = Field(..., description="Total estimated trip cost")
    currency: str = Field(default="USD", description="Currency code")

class ChatMessage(BaseModel):
    """Individual chat message."""
    role: Literal["user", "model"] = Field(..., description="Message role")
    text: str = Field(..., description="Message content")

class ChatRequestDto(BaseModel):
    """Request DTO for Co-Pilot chat."""
    message: str = Field(..., description="User message")
    conversation_history: Optional[List[ChatMessage]] = Field(None, description="Previous conversation")
    user_context: Optional[dict] = Field(None, description="User context for personalized responses")

class ChatResponseDto(BaseModel):
    """Response DTO for Co-Pilot chat."""
    reply: str = Field(..., description="AI assistant reply")
    category_analysis: Optional[dict] = Field(None, description="Spending analysis if requested")

class AutotrainFeedbackDto(BaseModel):
    """DTO for autotraining feedback from Pub/Sub."""
    user_id: str = Field(..., description="User ID for personalized training")
    merchant_text: str = Field(..., description="Original merchant text")
    predicted_category: str = Field(..., description="AI predicted category")
    user_corrected_category: str = Field(..., description="User's corrected category")
    timestamp: str = Field(..., description="Correction timestamp")