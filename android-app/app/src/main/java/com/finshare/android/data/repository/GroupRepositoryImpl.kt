package com.finshare.android.data.repository

import com.finshare.android.data.remote.FinShareApiService
import com.finshare.android.data.remote.dto.CreateGroupRequest
import com.finshare.android.domain.model.Group
import com.finshare.android.domain.repository.GroupRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GroupRepository using Clean Architecture
 * Handles data operations and DTO to domain model mapping
 */
@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val apiService: FinShareApiService
) : GroupRepository {

    override suspend fun getUserGroups(): List<Group> {
        return try {
            val response = apiService.getUserGroups("Bearer mock-token")
            if (response.isSuccessful) {
                response.body()?.map { dto ->
                    Group(
                        id = dto.id,
                        name = dto.name,
                        imageUrl = dto.imageUrl,
                        createdBy = dto.createdBy,
                        createdAt = LocalDateTime.parse(dto.createdAt),
                        updatedAt = dto.updatedAt?.let { LocalDateTime.parse(it) },
                        members = dto.members
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Return empty list for development - in production would handle errors properly
            emptyList()
        }
    }

    override suspend fun getGroup(groupId: String): Group? {
        return try {
            val response = apiService.getGroup("Bearer mock-token", groupId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Group(
                        id = dto.id,
                        name = dto.name,
                        imageUrl = dto.imageUrl,
                        createdBy = dto.createdBy,
                        createdAt = LocalDateTime.parse(dto.createdAt),
                        updatedAt = dto.updatedAt?.let { LocalDateTime.parse(it) },
                        members = dto.members
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createGroup(name: String, imageUrl: String?): Group {
        val request = CreateGroupRequest(name = name, imageUrl = imageUrl)
        val response = apiService.createGroup("Bearer mock-token", request)
        
        if (response.isSuccessful) {
            val dto = response.body()!!
            return Group(
                id = dto.id,
                name = dto.name,
                imageUrl = dto.imageUrl,
                createdBy = dto.createdBy,
                createdAt = LocalDateTime.parse(dto.createdAt),
                updatedAt = dto.updatedAt?.let { LocalDateTime.parse(it) },
                members = dto.members
            )
        } else {
            throw Exception("Failed to create group")
        }
    }

    override suspend fun addMember(groupId: String, memberEmail: String) {
        apiService.addGroupMember("Bearer mock-token", groupId, memberEmail)
    }

    override suspend fun deleteGroup(groupId: String) {
        // Would implement delete endpoint when available
        throw NotImplementedError("Delete group not implemented yet")
    }
}