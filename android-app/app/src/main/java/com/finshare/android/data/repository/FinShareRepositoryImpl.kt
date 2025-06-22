package com.finshare.android.data.repository

import com.finshare.android.data.network.FinShareApiService
import com.finshare.android.domain.model.*
import com.finshare.android.domain.repository.FinShareRepository
import com.finshare.android.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinShareRepositoryImpl @Inject constructor(
    private val apiService: FinShareApiService
) : FinShareRepository {

    override fun getGroups(): Flow<Resource<List<Group>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getGroups()
            if (response.isSuccessful) {
                response.body()?.let { groups ->
                    emit(Resource.Success(groups))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun createGroup(request: CreateGroupRequest): Flow<Resource<Group>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createGroup(request)
            if (response.isSuccessful) {
                response.body()?.let { group ->
                    emit(Resource.Success(group))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getExpenses(): Flow<Resource<List<Expense>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getExpenses()
            if (response.isSuccessful) {
                response.body()?.let { expenses ->
                    emit(Resource.Success(expenses))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun createExpense(request: CreateExpenseRequest): Flow<Resource<Expense>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createExpense(request)
            if (response.isSuccessful) {
                response.body()?.let { expense ->
                    emit(Resource.Success(expense))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun categorizeExpense(request: CategorizeRequest): Flow<Resource<CategoryResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.categorizeExpense(request)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    emit(Resource.Success(result))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun generateTripBudget(request: TripBudgetRequest): Flow<Resource<TripBudgetResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.generateTripBudget(request)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    emit(Resource.Success(result))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun chatWithCopilot(request: ChatRequest): Flow<Resource<ChatResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.chatWithCopilot(request)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    emit(Resource.Success(result))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getBudgets(): Flow<Resource<List<Budget>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getBudgets()
            if (response.isSuccessful) {
                response.body()?.let { budgets ->
                    emit(Resource.Success(budgets))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun createBudget(request: CreateBudgetRequest): Flow<Resource<Budget>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createBudget(request)
            if (response.isSuccessful) {
                response.body()?.let { budget ->
                    emit(Resource.Success(budget))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getSettlements(): Flow<Resource<List<Settlement>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getSettlements()
            if (response.isSuccessful) {
                response.body()?.let { settlements ->
                    emit(Resource.Success(settlements))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNotifications()
            if (response.isSuccessful) {
                response.body()?.let { notifications ->
                    emit(Resource.Success(notifications))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }
}