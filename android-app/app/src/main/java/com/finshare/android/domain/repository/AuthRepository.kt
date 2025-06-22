package com.finshare.android.domain.repository

import com.finshare.android.domain.model.AuthResult
import com.finshare.android.domain.model.User
import com.finshare.android.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<Resource<User?>>
    suspend fun signInWithGoogle(idToken: String): Resource<AuthResult>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<AuthResult>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Resource<AuthResult>
    suspend fun signOut(): Resource<Unit>
    suspend fun getIdToken(): Resource<String>
}