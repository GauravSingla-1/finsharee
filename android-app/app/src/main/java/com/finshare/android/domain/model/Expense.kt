package com.finshare.android.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val expenseId: String,
    val description: String,
    val amount: Double,
    val category: String? = null,
    val groupId: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String? = null,
    val splitMethod: SplitMethod,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    val nextDueDate: String? = null,
    val payers: List<ExpensePayer> = emptyList(),
    val splits: List<ExpenseSplit> = emptyList()
)

@Serializable
enum class SplitMethod {
    EQUAL, EXACT, PERCENTAGE, SHARES
}

@Serializable
data class ExpensePayer(
    val id: String,
    val userId: String,
    val amount: Double,
    val expenseId: String
)

@Serializable
data class ExpenseSplit(
    val id: String,
    val userId: String,
    val amount: Double,
    val percentage: Double? = null,
    val shares: Int? = null,
    val expenseId: String
)

@Serializable
data class CreateExpenseRequest(
    val description: String,
    val amount: Double,
    val category: String? = null,
    val groupId: String,
    val createdBy: String,
    val splitMethod: SplitMethod = SplitMethod.EQUAL,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null
)