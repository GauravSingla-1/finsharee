package com.finshare.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finshare.android.domain.model.*
import com.finshare.android.domain.repository.FinShareRepository
import com.finshare.android.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FinShareRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<Resource<List<Group>>>(Resource.Loading())
    val groups: StateFlow<Resource<List<Group>>> = _groups.asStateFlow()

    private val _expenses = MutableStateFlow<Resource<List<Expense>>>(Resource.Loading())
    val expenses: StateFlow<Resource<List<Expense>>> = _expenses.asStateFlow()

    private val _budgets = MutableStateFlow<Resource<List<Budget>>>(Resource.Loading())
    val budgets: StateFlow<Resource<List<Budget>>> = _budgets.asStateFlow()

    private val _settlements = MutableStateFlow<Resource<List<Settlement>>>(Resource.Loading())
    val settlements: StateFlow<Resource<List<Settlement>>> = _settlements.asStateFlow()

    private val _notifications = MutableStateFlow<Resource<List<Notification>>>(Resource.Loading())
    val notifications: StateFlow<Resource<List<Notification>>> = _notifications.asStateFlow()

    private val _categorizeResult = MutableStateFlow<Resource<CategoryResponse>?>(null)
    val categorizeResult: StateFlow<Resource<CategoryResponse>?> = _categorizeResult.asStateFlow()

    private val _tripBudgetResult = MutableStateFlow<Resource<TripBudgetResponse>?>(null)
    val tripBudgetResult: StateFlow<Resource<TripBudgetResponse>?> = _tripBudgetResult.asStateFlow()

    private val _chatResult = MutableStateFlow<Resource<ChatResponse>?>(null)
    val chatResult: StateFlow<Resource<ChatResponse>?> = _chatResult.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadGroups()
        loadExpenses()
        loadBudgets()
        loadSettlements()
        loadNotifications()
    }

    fun loadGroups() {
        viewModelScope.launch {
            repository.getGroups().collect { result ->
                _groups.value = result
            }
        }
    }

    fun createGroup(groupName: String, userId: String = "android-user-123") {
        viewModelScope.launch {
            val request = CreateGroupRequest(
                groupName = groupName,
                createdBy = userId
            )
            repository.createGroup(request).collect { result ->
                if (result is Resource.Success) {
                    loadGroups() // Refresh groups list
                }
            }
        }
    }

    fun loadExpenses() {
        viewModelScope.launch {
            repository.getExpenses().collect { result ->
                _expenses.value = result
            }
        }
    }

    fun createExpense(
        description: String,
        amount: Double,
        category: String?,
        groupId: String,
        userId: String = "android-user-123"
    ) {
        viewModelScope.launch {
            val request = CreateExpenseRequest(
                description = description,
                amount = amount,
                category = category,
                groupId = groupId,
                createdBy = userId
            )
            repository.createExpense(request).collect { result ->
                if (result is Resource.Success) {
                    loadExpenses() // Refresh expenses list
                }
            }
        }
    }

    fun categorizeExpense(merchantText: String, transactionType: String = "DEBIT", amount: Double? = null) {
        viewModelScope.launch {
            val request = CategorizeRequest(
                merchant_text = merchantText,
                transaction_type = transactionType,
                amount = amount
            )
            repository.categorizeExpense(request).collect { result ->
                _categorizeResult.value = result
            }
        }
    }

    fun generateTripBudget(
        description: String,
        destination: String? = null,
        duration: Int? = null,
        budgetRange: String? = null
    ) {
        viewModelScope.launch {
            val request = TripBudgetRequest(
                prompt_text = description,
                destination = destination,
                duration_days = duration,
                budget_range = budgetRange
            )
            repository.generateTripBudget(request).collect { result ->
                _tripBudgetResult.value = result
            }
        }
    }

    fun chatWithCopilot(message: String, conversationHistory: List<ChatMessage>? = null) {
        viewModelScope.launch {
            val request = ChatRequest(
                message = message,
                conversation_history = conversationHistory,
                user_context = mapOf("userId" to "android-user-123")
            )
            repository.chatWithCopilot(request).collect { result ->
                _chatResult.value = result
            }
        }
    }

    fun loadBudgets() {
        viewModelScope.launch {
            repository.getBudgets().collect { result ->
                _budgets.value = result
            }
        }
    }

    fun createBudget(
        category: String,
        amount: Double,
        period: BudgetPeriod,
        userId: String = "android-user-123"
    ) {
        viewModelScope.launch {
            val request = CreateBudgetRequest(
                userId = userId,
                category = category,
                amount = amount,
                period = period
            )
            repository.createBudget(request).collect { result ->
                if (result is Resource.Success) {
                    loadBudgets() // Refresh budgets list
                }
            }
        }
    }

    fun loadSettlements() {
        viewModelScope.launch {
            repository.getSettlements().collect { result ->
                _settlements.value = result
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            repository.getNotifications().collect { result ->
                _notifications.value = result
            }
        }
    }

    fun clearCategorizeResult() {
        _categorizeResult.value = null
    }

    fun clearTripBudgetResult() {
        _tripBudgetResult.value = null
    }

    fun clearChatResult() {
        _chatResult.value = null
    }
}