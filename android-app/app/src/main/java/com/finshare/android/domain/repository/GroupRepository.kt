package com.finshare.android.domain.repository

import com.finshare.android.domain.model.Group

/**
 * Repository interface for Group domain following Clean Architecture
 */
interface GroupRepository {
    suspend fun getUserGroups(): List<Group>
    suspend fun getGroup(groupId: String): Group?
    suspend fun createGroup(name: String, imageUrl: String?): Group
    suspend fun addMember(groupId: String, memberEmail: String)
    suspend fun deleteGroup(groupId: String)
}