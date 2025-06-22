package com.finshare.android.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Domain model representing a Group for expense sharing
 */
@Serializable
data class Group(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val members: List<String> = emptyList()
)