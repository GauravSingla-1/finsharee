package com.finshare.android.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val displayName: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val isEmailVerified: Boolean,
    val createdAt: String,
    val lastActive: String,
    val groupCount: Int,
    val status: String,
    val lastSynced: Long = System.currentTimeMillis()
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val groupId: String,
    val groupName: String,
    val createdBy: String,
    val groupImageUrl: String?,
    val createdAt: String,
    val updatedAt: String?,
    val members: List<String>,
    val needsSync: Boolean = false,
    val isNew: Boolean = false,
    val lastSynced: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val expenseId: String,
    val description: String,
    val amount: Double,
    val category: String?,
    val groupId: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String?,
    val splitMethod: String,
    val isRecurring: Boolean,
    val recurrenceRule: String?,
    val nextDueDate: String?,
    val needsSync: Boolean = false,
    val isNew: Boolean = false,
    val lastSynced: Long = System.currentTimeMillis()
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val budgetId: String,
    val userId: String,
    val category: String,
    val amount: Double,
    val period: String,
    val createdAt: String,
    val updatedAt: String?,
    val needsSync: Boolean = false,
    val isNew: Boolean = false,
    val lastSynced: Long = System.currentTimeMillis()
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val transactionId: String,
    val fromUserId: String,
    val toUserId: String,
    val amount: Double,
    val groupId: String,
    val expenseId: String,
    val isSettled: Boolean,
    val createdAt: String,
    val settledAt: String?,
    val lastSynced: Long = System.currentTimeMillis()
)

@Entity(tableName = "pending_transactions")
data class PendingTransactionEntity(
    @PrimaryKey val id: String,
    val merchantName: String,
    val amount: Double,
    val transactionType: String,
    val timestamp: Long,
    val rawMessage: String,
    val sender: String,
    val confidence: Float,
    val isProcessed: Boolean = false,
    val createdExpenseId: String? = null
)

// Type converters for complex types
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
}