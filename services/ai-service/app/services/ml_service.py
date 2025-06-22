"""
Machine Learning service for expense categorization and autotraining.
"""

import logging
from typing import Dict, List, Tuple, Optional
import json
import os

logger = logging.getLogger(__name__)

class MLCategorizer:
    """
    ML-powered expense categorization service with autotraining capabilities.
    
    Uses a lightweight classification approach with fallback rules for development.
    In production, this would integrate with pre-trained transformer models.
    """
    
    def __init__(self):
        self.categories = [
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
        
        # Merchant keyword mappings for rule-based fallback
        self.category_keywords = {
            "Food & Dining": [
                "restaurant", "cafe", "coffee", "pizza", "burger", "starbucks", 
                "mcdonald", "subway", "kfc", "domino", "food", "dining", "kitchen",
                "bistro", "bar", "pub", "grill", "diner"
            ],
            "Transportation": [
                "uber", "lyft", "taxi", "bus", "metro", "train", "flight", 
                "airline", "airport", "parking", "toll", "transit", "transport"
            ],
            "Shopping": [
                "amazon", "walmart", "target", "store", "mall", "shop", "retail",
                "clothing", "shoes", "electronics", "best buy", "costco"
            ],
            "Entertainment": [
                "movie", "theater", "cinema", "netflix", "spotify", "game",
                "concert", "show", "entertainment", "ticket", "event"
            ],
            "Bills & Utilities": [
                "electric", "gas", "water", "internet", "phone", "utility",
                "bill", "payment", "insurance", "rent", "mortgage"
            ],
            "Healthcare": [
                "hospital", "doctor", "pharmacy", "medical", "health", "clinic",
                "dentist", "medicine", "prescription", "cvs", "walgreens"
            ],
            "Travel": [
                "hotel", "booking", "airbnb", "resort", "vacation", "trip",
                "travel", "expedia", "tripadvisor"
            ],
            "Gas & Fuel": [
                "gas", "fuel", "shell", "chevron", "bp", "exxon", "mobil",
                "station", "petrol"
            ],
            "Groceries": [
                "grocery", "supermarket", "safeway", "kroger", "whole foods",
                "trader joe", "market", "produce"
            ],
            "Education": [
                "school", "university", "college", "education", "tuition",
                "book", "course", "learning"
            ],
            "Business": [
                "office", "supplies", "business", "conference", "meeting",
                "professional", "software", "subscription"
            ]
        }
        
        self.user_training_data = {}  # User-specific training data
        
    async def initialize(self):
        """Initialize the ML categorizer."""
        logger.info("Initializing ML Categorizer with rule-based fallback")
        # In production, this would load pre-trained models
        return True
        
    def categorize_transaction(self, merchant_text: str, transaction_type: str = "DEBIT", amount: Optional[float] = None) -> Tuple[str, float]:
        """
        Categorize a transaction based on merchant text.
        
        Returns:
            Tuple of (predicted_category, confidence_score)
        """
        merchant_lower = merchant_text.lower()
        
        # Score each category based on keyword matches
        category_scores = {}
        
        for category, keywords in self.category_keywords.items():
            score = 0
            for keyword in keywords:
                if keyword in merchant_lower:
                    # Exact match gets higher score
                    if keyword == merchant_lower:
                        score += 1.0
                    # Partial match gets lower score
                    elif keyword in merchant_lower:
                        score += 0.7
                    # Word boundary match gets medium score
                    elif f" {keyword} " in f" {merchant_lower} ":
                        score += 0.8
            
            if score > 0:
                category_scores[category] = score
        
        if category_scores:
            # Get category with highest score
            best_category = max(category_scores, key=category_scores.get)
            confidence = min(category_scores[best_category], 1.0)
            
            # Adjust confidence based on multiple matches
            if len([s for s in category_scores.values() if s > 0.5]) > 1:
                confidence *= 0.8  # Lower confidence if multiple categories match
                
            return best_category, confidence
        
        # Default fallback
        return "Other", 0.3
        
    def get_alternative_categories(self, merchant_text: str, top_k: int = 3) -> List[str]:
        """Get alternative category suggestions."""
        merchant_lower = merchant_text.lower()
        category_scores = {}
        
        for category, keywords in self.category_keywords.items():
            score = sum(0.5 for keyword in keywords if keyword in merchant_lower)
            if score > 0:
                category_scores[category] = score
        
        # Return top k categories excluding the primary prediction
        sorted_categories = sorted(category_scores.items(), key=lambda x: x[1], reverse=True)
        return [cat for cat, _ in sorted_categories[:top_k]]
        
    def add_training_example(self, user_id: str, merchant_text: str, correct_category: str):
        """Add a training example for user-specific model improvement."""
        if user_id not in self.user_training_data:
            self.user_training_data[user_id] = []
            
        self.user_training_data[user_id].append({
            "merchant_text": merchant_text,
            "category": correct_category
        })
        
        logger.info(f"Added training example for user {user_id}: {merchant_text} -> {correct_category}")
        
        # Trigger retraining if enough examples collected
        if len(self.user_training_data[user_id]) % 10 == 0:
            self._update_user_model(user_id)
            
    def _update_user_model(self, user_id: str):
        """Update user-specific categorization model."""
        user_data = self.user_training_data.get(user_id, [])
        if len(user_data) < 5:
            return
            
        logger.info(f"Updating personalized model for user {user_id} with {len(user_data)} examples")
        
        # In a production system, this would:
        # 1. Prepare training data
        # 2. Fine-tune the model with user-specific examples
        # 3. Save the personalized model
        # 4. Update the inference pipeline
        
        # For now, we'll update keyword weights based on user corrections
        self._update_keyword_weights(user_id, user_data)
        
    def _update_keyword_weights(self, user_id: str, training_data: List[dict]):
        """Update keyword weights based on user corrections."""
        # Simple approach: boost keywords that appear in corrected categories
        category_keyword_boosts = {}
        
        for example in training_data:
            merchant = example["merchant_text"].lower()
            category = example["category"]
            
            if category not in category_keyword_boosts:
                category_keyword_boosts[category] = []
                
            # Extract meaningful words from merchant text
            words = [word.strip() for word in merchant.split() if len(word) > 2]
            category_keyword_boosts[category].extend(words)
        
        # Update category keywords with user-specific terms
        for category, new_keywords in category_keyword_boosts.items():
            if category in self.category_keywords:
                # Add frequently corrected terms
                unique_keywords = list(set(new_keywords))
                self.category_keywords[category].extend(unique_keywords[:3])  # Add top 3
                
        logger.info(f"Updated keyword weights for user {user_id}")