package com.finshare.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val displayName: String,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val createdAt: String,
    val isEmailVerified: Boolean = false
)