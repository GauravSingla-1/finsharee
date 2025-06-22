package com.finshare.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SettlementDto(
    val transactionId: String,
    val fromUserId: String,
    val toUserId: String,
    val amount: Double,
    val groupId: String,
    val expenseId: String,
    val isSettled: Boolean,
    val createdAt: String,
    val settledAt: String?
)