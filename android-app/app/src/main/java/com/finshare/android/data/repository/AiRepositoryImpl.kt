package com.finshare.android.data.repository

import com.finshare.android.data.remote.FinShareApiService
import com.finshare.android.domain.model.*
import com.finshare.android.domain.repository.AiRepository
import com.finshare.android.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val apiService: FinShareApiService
) : AiRepository {

    override suspend fun categorizeExpense(request: CategoryRequest): Resource<CategoryResponse> {
        return try {
            val response = apiService.categorizeExpense(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to categorize expense")
        }
    }

    override suspend fun generateTripBudget(request: TripBudgetRequest): Resource<TripBudgetResponse> {
        return try {
            val response = apiService.generateTripBudget(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate trip budget")
        }
    }

    override suspend fun chatWithCoPilot(
        message: String,
        conversationHistory: List<ChatMessage>
    ): Resource<ChatResponse> {
        return try {
            val request = ChatRequest(
                message = message,
                conversation_history = conversationHistory
            )
            val response = apiService.chatWithCoPilot(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to chat with AI Co-Pilot")
        }
    }
}