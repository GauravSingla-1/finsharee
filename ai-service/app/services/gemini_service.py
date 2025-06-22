"""
Google Gemini integration service for Co-Pilot features.
Provides privacy-preserving gateway to Gemini API.
"""

import logging
import os
import json
from typing import Dict, List, Optional
import httpx

logger = logging.getLogger(__name__)

class GeminiService:
    """
    Privacy-preserving gateway to Google Gemini API for Co-Pilot features.
    
    Ensures user data privacy by:
    - Aggregating personal data before sending to external APIs
    - Using anonymized spending summaries instead of raw transactions
    - Applying prompt engineering for context-rich responses
    """
    
    def __init__(self):
        self.api_key = os.getenv("GOOGLE_GEMINI_API_KEY")
        self.analytics_service_url = os.getenv("ANALYTICS_SERVICE_URL", "http://localhost:8005")
        self.base_url = "https://generativelanguage.googleapis.com/v1beta"
        
        if not self.api_key:
            logger.warning("Google Gemini API key not found. Using mock responses for development.")
            self.use_mock = True
        else:
            self.use_mock = False
            
    async def generate_trip_budget(self, prompt_text: str, destination: Optional[str] = None, 
                                 duration_days: Optional[int] = None, budget_range: Optional[str] = None) -> Dict:
        """
        Generate a detailed trip budget using Gemini API.
        
        Args:
            prompt_text: Natural language trip description
            destination: Trip destination
            duration_days: Trip duration in days
            budget_range: Budget range preference
            
        Returns:
            Structured budget with itemized costs
        """
        
        # Construct enhanced prompt with context
        context_prompt = self._build_trip_budget_prompt(prompt_text, destination, duration_days, budget_range)
        
        if self.use_mock:
            return self._mock_trip_budget_response(destination or "destination", duration_days or 3)
            
        try:
            response = await self._call_gemini_api(context_prompt, format_as_json=True)
            return self._parse_trip_budget_response(response)
        except Exception as e:
            logger.error(f"Gemini API call failed: {e}")
            return self._mock_trip_budget_response(destination or "destination", duration_days or 3)
            
    async def chat_with_copilot(self, message: str, conversation_history: Optional[List[Dict]] = None,
                              user_id: Optional[str] = None) -> str:
        """
        Handle Co-Pilot chat requests with privacy-preserving context enrichment.
        
        Args:
            message: User's message
            conversation_history: Previous conversation context
            user_id: User ID for personalized responses (if available)
            
        Returns:
            AI assistant response
        """
        
        # Check if the query is about personal spending
        if self._is_spending_query(message) and user_id:
            # Fetch anonymized spending data
            spending_context = await self._get_anonymized_spending_data(user_id)
            enhanced_prompt = self._build_spending_advice_prompt(message, spending_context)
        else:
            enhanced_prompt = message
            
        if self.use_mock:
            return self._mock_chat_response(message)
            
        try:
            # Include conversation history for context
            full_prompt = self._build_conversation_prompt(enhanced_prompt, conversation_history)
            response = await self._call_gemini_api(full_prompt)
            return response
        except Exception as e:
            logger.error(f"Gemini chat API call failed: {e}")
            return self._mock_chat_response(message)
            
    def _build_trip_budget_prompt(self, prompt_text: str, destination: Optional[str], 
                                duration_days: Optional[int], budget_range: Optional[str]) -> str:
        """Build enhanced prompt for trip budget generation."""
        
        base_prompt = f"""You are a travel budget planning expert. Create a detailed, realistic budget for the following trip:

Trip Description: {prompt_text}
"""
        
        if destination:
            base_prompt += f"Destination: {destination}\n"
        if duration_days:
            base_prompt += f"Duration: {duration_days} days\n"
        if budget_range:
            base_prompt += f"Budget Range: {budget_range}\n"
            
        base_prompt += """
Please provide a structured budget in JSON format with the following categories:
- Accommodation
- Transportation
- Food & Dining
- Entertainment & Activities
- Shopping & Souvenirs
- Miscellaneous

For each category, include:
- estimated_cost (number)
- description (string explaining the estimate)

Format the response as valid JSON with a "budget_items" array and "total_estimated_cost" field.
"""
        
        return base_prompt
        
    def _build_spending_advice_prompt(self, message: str, spending_context: Dict) -> str:
        """Build prompt with anonymized spending context for personalized advice."""
        
        context_summary = ""
        if spending_context:
            context_summary = f"""
User's Spending Context (Last 30 Days):
{json.dumps(spending_context, indent=2)}
"""
        
        return f"""You are a personal finance advisor. A user is asking for spending advice.

{context_summary}

User's Question: "{message}"

Provide 3-5 actionable, personalized tips based on their spending patterns. Be specific and practical.
"""
        
    def _build_conversation_prompt(self, message: str, history: Optional[List[Dict]]) -> str:
        """Build prompt with conversation history."""
        
        if not history:
            return message
            
        conversation = "Previous conversation:\n"
        for msg in history[-5:]:  # Include last 5 messages for context
            role = "User" if msg["role"] == "user" else "Assistant"
            conversation += f"{role}: {msg['text']}\n"
            
        return f"{conversation}\nUser: {message}\nAssistant:"
        
    async def _call_gemini_api(self, prompt: str, format_as_json: bool = False) -> str:
        """Make API call to Gemini."""
        
        url = f"{self.base_url}/models/gemini-pro:generateContent"
        
        headers = {"Content-Type": "application/json"}
        
        payload = {
            "contents": [{
                "parts": [{"text": prompt}]
            }],
            "generationConfig": {
                "temperature": 0.7,
                "maxOutputTokens": 1024,
            }
        }
        
        if format_as_json:
            payload["generationConfig"]["response_mime_type"] = "application/json"
            
        async with httpx.AsyncClient() as client:
            response = await client.post(
                url, 
                json=payload, 
                headers=headers,
                params={"key": self.api_key},
                timeout=30
            )
            
            if response.status_code == 200:
                result = response.json()
                return result["candidates"][0]["content"]["parts"][0]["text"]
            else:
                raise Exception(f"Gemini API error: {response.status_code} - {response.text}")
                
    async def _get_anonymized_spending_data(self, user_id: str) -> Dict:
        """Fetch anonymized spending data from Analytics Service."""
        
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.analytics_service_url}/api/analytics/spending-summary/{user_id}",
                    timeout=5
                )
                
                if response.status_code == 200:
                    return response.json()
                    
        except Exception as e:
            logger.warning(f"Could not fetch spending data: {e}")
            
        # Return mock data for development
        return {
            "categories": {
                "Food & Dining": {"amount": 450, "percentage": 22},
                "Transportation": {"amount": 200, "percentage": 10},
                "Shopping": {"amount": 300, "percentage": 15},
                "Entertainment": {"amount": 150, "percentage": 7}
            },
            "total_spending_30d": 1100,
            "avg_daily_spending": 36.67
        }
        
    def _is_spending_query(self, message: str) -> bool:
        """Check if the message is asking about spending/budgeting."""
        
        spending_keywords = [
            "spending", "budget", "save", "money", "expensive", "cost", 
            "afford", "financial", "income", "expense", "category", "reduce"
        ]
        
        message_lower = message.lower()
        return any(keyword in message_lower for keyword in spending_keywords)
        
    def _parse_trip_budget_response(self, response: str) -> Dict:
        """Parse Gemini's trip budget response."""
        
        try:
            # Try to parse as JSON
            return json.loads(response)
        except json.JSONDecodeError:
            # Fallback parsing logic
            logger.warning("Could not parse Gemini response as JSON, using fallback")
            return self._mock_trip_budget_response("destination", 3)
            
    def _mock_trip_budget_response(self, destination: str, duration: int) -> Dict:
        """Generate mock trip budget response for development."""
        
        base_cost_per_day = 100
        
        return {
            "budget_items": [
                {
                    "category": "Accommodation",
                    "estimated_cost": duration * 80,
                    "description": f"Hotel/lodging for {duration} nights"
                },
                {
                    "category": "Transportation", 
                    "estimated_cost": 200,
                    "description": "Flights and local transport"
                },
                {
                    "category": "Food & Dining",
                    "estimated_cost": duration * 50,
                    "description": "Meals and dining experiences"
                },
                {
                    "category": "Entertainment & Activities",
                    "estimated_cost": duration * 40,
                    "description": "Tours, attractions, and activities"
                },
                {
                    "category": "Shopping & Souvenirs",
                    "estimated_cost": 100,
                    "description": "Shopping and souvenirs"
                },
                {
                    "category": "Miscellaneous",
                    "estimated_cost": 100,
                    "description": "Tips, emergency fund, and other expenses"
                }
            ],
            "total_estimated_cost": duration * base_cost_per_day + 400,
            "currency": "USD"
        }
        
    def _mock_chat_response(self, message: str) -> str:
        """Generate mock chat response for development."""
        
        if self._is_spending_query(message):
            return """Based on your spending patterns, here are some personalized tips:

1. **Food & Dining (22% of spending)**: Consider meal prepping and cooking at home more often. You could save $100-150/month by reducing restaurant visits by just 2-3 times per week.

2. **Transportation costs**: Look into monthly transit passes or carpooling options if you're using rideshare frequently.

3. **Set category budgets**: Try the 50/30/20 rule - 50% for needs, 30% for wants, 20% for savings.

4. **Track daily spending**: Small purchases add up quickly. Try setting a daily spending limit.

5. **Review subscriptions**: Cancel unused subscriptions and services you're not actively using.

Would you like specific advice on any particular spending category?"""
        
        return "I'm here to help with your financial questions and trip planning. What would you like to know more about?"