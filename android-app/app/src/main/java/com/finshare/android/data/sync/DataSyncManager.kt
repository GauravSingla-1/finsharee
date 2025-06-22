package com.finshare.android.data.sync

import com.finshare.android.data.local.database.FinShareDatabase
import com.finshare.android.data.remote.FinShareApiService
import com.finshare.android.data.security.SecureStorageManager
import com.finshare.android.domain.repository.AuthRepository
import com.finshare.android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSyncManager @Inject constructor(
    private val database: FinShareDatabase,
    private val apiService: FinShareApiService,
    private val authRepository: AuthRepository,
    private val secureStorageManager: SecureStorageManager
) {

    suspend fun performFullSync(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Check if user is authenticated
            val tokenResult = authRepository.getIdToken()
            if (tokenResult !is Resource.Success) {
                return@withContext false
            }

            // Sync in order: Groups -> Expenses -> Settlements -> Analytics
            val syncResults = listOf(
                syncGroups(),
                syncExpenses(),
                syncSettlements(),
                syncBudgets()
            )

            // Update last sync timestamp if all successful
            if (syncResults.all { it }) {
                secureStorageManager.storeLastSyncTimestamp(System.currentTimeMillis())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncGroups(): Boolean {
        return try {
            // Get local groups that need sync
            val localGroups = database.groupDao().getAllGroups()
            
            // Upload new/modified groups
            localGroups.filter { it.needsSync }.forEach { group ->
                try {
                    val response = if (group.isNew) {
                        apiService.createGroup(group.toCreateRequest())
                    } else {
                        apiService.updateGroup(group.groupId, group.toUpdateRequest())
                    }
                    
                    // Update local group with server response
                    database.groupDao().updateGroup(
                        group.copy(
                            needsSync = false,
                            isNew = false
                        )
                    )
                } catch (e: Exception) {
                    // Log error but continue with other groups
                }
            }

            // Download latest groups from server
            val serverGroups = apiService.getGroups()
            
            // Update local database
            database.groupDao().insertGroups(
                serverGroups.map { it.toEntity() }
            )
            
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncExpenses(): Boolean {
        return try {
            // Similar sync logic for expenses
            val localExpenses = database.expenseDao().getAllExpenses()
            
            localExpenses.filter { it.needsSync }.forEach { expense ->
                try {
                    if (expense.isNew) {
                        apiService.createExpense(expense.toCreateRequest())
                    } else {
                        apiService.updateExpense(expense.expenseId, expense.toUpdateRequest())
                    }
                    
                    database.expenseDao().updateExpense(
                        expense.copy(needsSync = false, isNew = false)
                    )
                } catch (e: Exception) {
                    // Continue with next expense
                }
            }

            val serverExpenses = apiService.getExpenses()
            database.expenseDao().insertExpenses(
                serverExpenses.map { it.toEntity() }
            )
            
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncSettlements(): Boolean {
        return try {
            val settlements = apiService.getSettlements()
            database.settlementDao().insertSettlements(
                settlements.map { it.toEntity() }
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncBudgets(): Boolean {
        return try {
            val budgets = apiService.getBudgets()
            database.budgetDao().insertBudgets(
                budgets.map { it.toEntity() }
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getLastSyncTime(): Long {
        return secureStorageManager.getLastSyncTimestamp()
    }

    suspend fun needsSync(): Boolean {
        val lastSync = getLastSyncTime()
        val now = System.currentTimeMillis()
        val syncInterval = 15 * 60 * 1000L // 15 minutes
        
        return (now - lastSync) > syncInterval
    }
}