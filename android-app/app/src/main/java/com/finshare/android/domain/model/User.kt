package com.finshare.android.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a User in the FinShare application
 */
@Serializable
data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: String,
    val lastActive: String,
    val groupCount: Int = 0,
    val status: String = "ACTIVE"
)

@Serializable
data class AuthResult(
    val user: User,
    val token: String
)