package com.finshare.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String? = null,
    val members: List<String> = emptyList()
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val imageUrl: String? = null
)