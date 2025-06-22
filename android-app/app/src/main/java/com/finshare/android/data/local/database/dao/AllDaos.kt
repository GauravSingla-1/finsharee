package com.finshare.android.data.local.database.dao

import androidx.room.*
import com.finshare.android.data.local.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>
    
    @Query("SELECT * FROM groups")
    suspend fun getAllGroups(): List<GroupEntity>
    
    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    suspend fun getGroup(groupId: String): GroupEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)
    
    @Update
    suspend fun updateGroup(group: GroupEntity)
    
    @Delete
    suspend fun deleteGroup(group: GroupEntity)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<ExpenseEntity>
    
    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getExpensesByGroup(groupId: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId")
    suspend fun getExpense(expenseId: String): ExpenseEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)
    
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
    
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBudgetsByUser(userId: String): Flow<List<BudgetEntity>>
    
    @Query("SELECT * FROM budgets")
    suspend fun getAllBudgets(): List<BudgetEntity>
    
    @Query("SELECT * FROM budgets WHERE budgetId = :budgetId")
    suspend fun getBudget(budgetId: String): BudgetEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)
    
    @Update
    suspend fun updateBudget(budget: BudgetEntity)
    
    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)
}

@Dao
interface SettlementDao {
    @Query("SELECT * FROM settlements ORDER BY createdAt DESC")
    fun getAllSettlements(): Flow<List<SettlementEntity>>
    
    @Query("SELECT * FROM settlements")
    suspend fun getAllSettlements(): List<SettlementEntity>
    
    @Query("SELECT * FROM settlements WHERE groupId = :groupId")
    fun getSettlementsByGroup(groupId: String): Flow<List<SettlementEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlements(settlements: List<SettlementEntity>)
    
    @Update
    suspend fun updateSettlement(settlement: SettlementEntity)
    
    @Delete
    suspend fun deleteSettlement(settlement: SettlementEntity)
}

@Dao
interface PendingTransactionDao {
    @Query("SELECT * FROM pending_transactions WHERE isProcessed = 0 ORDER BY timestamp DESC")
    fun getPendingTransactions(): Flow<List<PendingTransactionEntity>>
    
    @Query("SELECT * FROM pending_transactions WHERE id = :id")
    suspend fun getTransaction(id: String): PendingTransactionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PendingTransactionEntity)
    
    @Update
    suspend fun updateTransaction(transaction: PendingTransactionEntity)
    
    @Query("UPDATE pending_transactions SET isProcessed = 1, createdExpenseId = :expenseId WHERE id = :id")
    suspend fun markAsProcessed(id: String, expenseId: String)
    
    @Delete
    suspend fun deleteTransaction(transaction: PendingTransactionEntity)
}