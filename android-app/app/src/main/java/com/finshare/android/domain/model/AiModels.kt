package com.finshare.android.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CategorizeRequest(
    val merchant_text: String,
    val transaction_type: String = "DEBIT",
    val amount: Double? = null
)

@Serializable
data class CategoryResponse(
    val predicted_category: String,
    val confidence_score: Double,
    val alternative_categories: List<String>? = null
)

@Serializable
data class TripBudgetRequest(
    val prompt_text: String,
    val destination: String? = null,
    val duration_days: Int? = null,
    val budget_range: String? = null
)

@Serializable
data class BudgetItem(
    val category: String,
    val estimated_cost: Double,
    val description: String
)

@Serializable
data class TripBudgetResponse(
    val budget_items: List<BudgetItem>,
    val total_estimated_cost: Double,
    val currency: String = "USD"
)

@Serializable
data class ChatMessage(
    val role: String,
    val text: String
)

@Serializable
data class ChatRequest(
    val message: String,
    val conversation_history: List<ChatMessage>? = null,
    val user_context: Map<String, String>? = null
)

@Serializable
data class ChatResponse(
    val reply: String,
    val category_analysis: Map<String, String>? = null
)