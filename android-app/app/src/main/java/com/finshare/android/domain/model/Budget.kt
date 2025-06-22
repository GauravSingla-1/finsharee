package com.finshare.android.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val budgetId: String,
    val userId: String,
    val category: String,
    val amount: Double,
    val period: BudgetPeriod,
    val createdAt: String,
    val updatedAt: String? = null
)

@Serializable
enum class BudgetPeriod {
    WEEKLY, MONTHLY
}

@Serializable
data class CreateBudgetRequest(
    val userId: String,
    val category: String,
    val amount: Double,
    val period: BudgetPeriod
)

@Serializable
data class Settlement(
    val transactionId: String,
    val fromUserId: String,
    val toUserId: String,
    val amount: Double,
    val expenseId: String,
    val groupId: String,
    val isSettled: Boolean = false,
    val createdAt: String,
    val settledAt: String? = null
)

@Serializable
data class CreateSettlementRequest(
    val fromUserId: String,
    val toUserId: String,
    val amount: Double,
    val expenseId: String,
    val groupId: String
)

@Serializable
data class Notification(
    val notificationId: String,
    val recipientId: String,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val timestamp: String,
    val metadata: Map<String, String> = emptyMap()
)