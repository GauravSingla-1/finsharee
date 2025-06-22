package com.finshare.android.domain.repository

import com.finshare.android.domain.model.*
import com.finshare.android.utils.Resource

interface AiRepository {
    suspend fun categorizeExpense(request: CategoryRequest): Resource<CategoryResponse>
    suspend fun generateTripBudget(request: TripBudgetRequest): Resource<TripBudgetResponse>
    suspend fun chatWithCoPilot(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): Resource<ChatResponse>
}