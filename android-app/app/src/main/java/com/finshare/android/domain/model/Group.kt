package com.finshare.android.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val groupId: String,
    val groupName: String,
    val groupImageUrl: String? = null,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String? = null,
    val members: List<String> = emptyList()
)

@Serializable
data class CreateGroupRequest(
    val groupName: String,
    val createdBy: String,
    val groupImageUrl: String? = null
)