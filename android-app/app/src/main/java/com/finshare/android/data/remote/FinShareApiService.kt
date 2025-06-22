package com.finshare.android.data.remote

import com.finshare.android.data.remote.dto.ExpenseDto
import com.finshare.android.data.remote.dto.GroupDto
import com.finshare.android.data.remote.dto.UserDto
import com.finshare.android.data.remote.dto.CreateExpenseRequest
import com.finshare.android.data.remote.dto.CreateGroupRequest
import com.finshare.android.data.remote.dto.CategoryResponse
import com.finshare.android.data.remote.dto.CategorizeRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for FinShare backend communication
 * Follows REST principles and integrates with microservices architecture
 */
interface FinShareApiService {

    // User Service APIs
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<UserDto>

    @GET("users/profile")
    suspend fun getCurrentUserProfile(@Header("Authorization") token: String): Response<UserDto>

    // Group Service APIs
    @GET("groups")
    suspend fun getUserGroups(@Header("Authorization") token: String): Response<List<GroupDto>>

    @POST("groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body request: CreateGroupRequest
    ): Response<GroupDto>

    @GET("groups/{groupId}")
    suspend fun getGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String
    ): Response<GroupDto>

    @POST("groups/{groupId}/members")
    suspend fun addGroupMember(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String,
        @Body memberEmail: String
    ): Response<Unit>

    // Expense Service APIs
    @GET("expenses/group/{groupId}")
    suspend fun getGroupExpenses(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String
    ): Response<List<ExpenseDto>>

    @POST("expenses")
    suspend fun createExpense(
        @Header("Authorization") token: String,
        @Body request: CreateExpenseRequest
    ): Response<ExpenseDto>

    @GET("expenses/{expenseId}")
    suspend fun getExpense(
        @Header("Authorization") token: String,
        @Path("expenseId") expenseId: String
    ): Response<ExpenseDto>

    @DELETE("expenses/{expenseId}")
    suspend fun deleteExpense(
        @Header("Authorization") token: String,
        @Path("expenseId") expenseId: String
    ): Response<Unit>

    // AI Service APIs
    @GET("ai/categories")
    suspend fun getExpenseCategories(): Response<CategoryResponse>

    @POST("ai/categorize")
    suspend fun categorizeExpense(@Body request: CategorizeRequest): Response<Map<String, Any>>

    // Balance Service APIs
    @GET("balances/group/{groupId}")
    suspend fun getGroupBalances(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String
    ): Response<Map<String, Any>>

    @POST("settlements")
    suspend fun settleBalance(
        @Header("Authorization") token: String,
        @Body settlement: Map<String, Any>
    ): Response<Unit>

    // Analytics Service APIs
    @GET("analytics/spending/{userId}")
    suspend fun getUserSpendingAnalytics(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<Map<String, Any>>

    @GET("budgets")
    suspend fun getUserBudgets(
        @Header("Authorization") token: String
    ): Response<List<Map<String, Any>>>
}