package com.finshare.android.domain.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing an Expense in a group
 */
@Serializable
data class Expense(
    val id: String,
    val groupId: String,
    val description: String,
    val amount: BigDecimal,
    val category: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val splitMethod: SplitMethod,
    val payers: List<ExpensePayer>,
    val splits: List<ExpenseSplit>,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    val nextDueDate: LocalDateTime? = null
)

@Serializable
data class ExpensePayer(
    val userId: String,
    val amount: BigDecimal
)

@Serializable
data class ExpenseSplit(
    val userId: String,
    val amount: BigDecimal,
    val percentage: Double? = null,
    val shares: Int? = null
)

@Serializable
enum class SplitMethod {
    EQUAL,
    EXACT,
    PERCENTAGE,
    SHARES
}