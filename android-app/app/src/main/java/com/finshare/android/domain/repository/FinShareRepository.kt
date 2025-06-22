package com.finshare.android.domain.repository

import com.finshare.android.domain.model.*
import com.finshare.android.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FinShareRepository {
    
    // Groups
    fun getGroups(): Flow<Resource<List<Group>>>
    fun createGroup(request: CreateGroupRequest): Flow<Resource<Group>>
    
    // Expenses
    fun getExpenses(): Flow<Resource<List<Expense>>>
    fun createExpense(request: CreateExpenseRequest): Flow<Resource<Expense>>
    
    // AI Services
    fun categorizeExpense(request: CategorizeRequest): Flow<Resource<CategoryResponse>>
    fun generateTripBudget(request: TripBudgetRequest): Flow<Resource<TripBudgetResponse>>
    fun chatWithCopilot(request: ChatRequest): Flow<Resource<ChatResponse>>
    
    // Analytics & Budgets
    fun getBudgets(): Flow<Resource<List<Budget>>>
    fun createBudget(request: CreateBudgetRequest): Flow<Resource<Budget>>
    
    // Settlements
    fun getSettlements(): Flow<Resource<List<Settlement>>>
    
    // Notifications
    fun getNotifications(): Flow<Resource<List<Notification>>>
}