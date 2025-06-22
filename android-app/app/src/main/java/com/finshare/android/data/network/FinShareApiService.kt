package com.finshare.android.data.network

import com.finshare.android.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinShareApiService {
    
    // Groups API
    @GET(ApiConstants.Endpoints.GROUPS)
    suspend fun getGroups(): Response<List<Group>>
    
    @POST(ApiConstants.Endpoints.GROUPS)
    suspend fun createGroup(
        @Body request: CreateGroupRequest
    ): Response<Group>
    
    @GET("${ApiConstants.Endpoints.GROUPS}/{groupId}")
    suspend fun getGroup(
        @Path("groupId") groupId: String
    ): Response<Group>
    
    @DELETE("${ApiConstants.Endpoints.GROUPS}/{groupId}")
    suspend fun deleteGroup(
        @Path("groupId") groupId: String
    ): Response<Unit>
    
    // Expenses API
    @GET(ApiConstants.Endpoints.EXPENSES)
    suspend fun getExpenses(): Response<List<Expense>>
    
    @POST(ApiConstants.Endpoints.EXPENSES)
    suspend fun createExpense(
        @Body request: CreateExpenseRequest
    ): Response<Expense>
    
    @GET("${ApiConstants.Endpoints.EXPENSES}/{expenseId}")
    suspend fun getExpense(
        @Path("expenseId") expenseId: String
    ): Response<Expense>
    
    @DELETE("${ApiConstants.Endpoints.EXPENSES}/{expenseId}")
    suspend fun deleteExpense(
        @Path("expenseId") expenseId: String
    ): Response<Unit>
    
    // AI Services
    @POST(ApiConstants.Endpoints.AI_CATEGORIZE)
    suspend fun categorizeExpense(
        @Body request: CategorizeRequest
    ): Response<CategoryResponse>
    
    @POST(ApiConstants.Endpoints.AI_TRIP_BUDGET)
    suspend fun generateTripBudget(
        @Body request: TripBudgetRequest
    ): Response<TripBudgetResponse>
    
    @POST(ApiConstants.Endpoints.AI_CHAT)
    suspend fun chatWithCopilot(
        @Body request: ChatRequest
    ): Response<ChatResponse>
    
    // Budget & Analytics
    @GET(ApiConstants.Endpoints.ANALYTICS_BUDGETS)
    suspend fun getBudgets(): Response<List<Budget>>
    
    @POST(ApiConstants.Endpoints.ANALYTICS_BUDGETS)
    suspend fun createBudget(
        @Body request: CreateBudgetRequest
    ): Response<Budget>
    
    @DELETE("${ApiConstants.Endpoints.ANALYTICS_BUDGETS}/{budgetId}")
    suspend fun deleteBudget(
        @Path("budgetId") budgetId: String
    ): Response<Unit>
    
    @GET("${ApiConstants.Endpoints.ANALYTICS_INSIGHTS}/monthly-overview")
    suspend fun getMonthlyOverview(): Response<Map<String, Any>>
    
    @GET("${ApiConstants.Endpoints.ANALYTICS_INSIGHTS}/category-breakdown")
    suspend fun getCategoryBreakdown(): Response<Map<String, Any>>
    
    // Settlements
    @GET(ApiConstants.Endpoints.SETTLEMENTS)
    suspend fun getSettlements(): Response<List<Settlement>>
    
    @POST(ApiConstants.Endpoints.SETTLEMENTS)
    suspend fun createSettlement(
        @Body request: CreateSettlementRequest
    ): Response<Settlement>
    
    @PUT("${ApiConstants.Endpoints.SETTLEMENTS}/{transactionId}/settle")
    suspend fun settleTransaction(
        @Path("transactionId") transactionId: String
    ): Response<Settlement>
    
    // Notifications
    @GET(ApiConstants.Endpoints.NOTIFICATIONS)
    suspend fun getNotifications(): Response<List<Notification>>
    
    @POST("${ApiConstants.Endpoints.NOTIFICATIONS}/send")
    suspend fun sendNotification(
        @Body notification: Notification
    ): Response<Notification>
}