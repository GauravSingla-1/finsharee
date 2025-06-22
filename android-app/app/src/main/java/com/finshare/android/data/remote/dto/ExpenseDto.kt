package com.finshare.android.data.remote.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class ExpenseDto(
    val id: String,
    val groupId: String,
    val description: String,
    val amount: String, // Using String for BigDecimal serialization
    val category: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String? = null,
    val splitMethod: String,
    val payers: List<ExpensePayerDto>,
    val splits: List<ExpenseSplitDto>,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    val nextDueDate: String? = null
)

@Serializable
data class ExpensePayerDto(
    val userId: String,
    val amount: String
)

@Serializable
data class ExpenseSplitDto(
    val userId: String,
    val amount: String,
    val percentage: Double? = null,
    val shares: Int? = null
)

@Serializable
data class CreateExpenseRequest(
    val groupId: String,
    val description: String,
    val amount: String,
    val category: String,
    val splitMethod: String,
    val payers: List<ExpensePayerDto>,
    val splits: List<ExpenseSplitDto>,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null
)

@Serializable
data class CategoryResponse(
    val categories: List<String>
)

@Serializable
data class CategorizeRequest(
    val merchant_text: String,
    val transaction_type: String = "DEBIT",
    val amount: Double? = null
)