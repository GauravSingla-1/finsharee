package com.finshare.android.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a User in the FinShare application
 */
@Serializable
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val createdAt: String,
    val isEmailVerified: Boolean = false
)