package com.finshare.android.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import com.finshare.android.domain.model.AuthResult
import com.finshare.android.domain.model.User
import com.finshare.android.domain.repository.AuthRepository
import com.finshare.android.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class FirebaseAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    override fun getCurrentUser(): Flow<Resource<User?>> = flow {
        emit(Resource.Loading())
        
        try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val user = mapFirebaseUserToUser(firebaseUser)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<AuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = mapFirebaseUserToUser(firebaseUser)
                val token = firebaseUser.getIdToken(false).await().token
                Resource.Success(AuthResult(user = user, token = token ?: ""))
            } else {
                Resource.Error("Authentication failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed")
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<AuthResult> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = mapFirebaseUserToUser(firebaseUser)
                val token = firebaseUser.getIdToken(false).await().token
                Resource.Success(AuthResult(user = user, token = token ?: ""))
            } else {
                Resource.Error("Authentication failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Email sign-in failed")
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Resource<AuthResult> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = mapFirebaseUserToUser(firebaseUser)
                val token = firebaseUser.getIdToken(false).await().token
                Resource.Success(AuthResult(user = user, token = token ?: ""))
            } else {
                Resource.Error("Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Email sign-up failed")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            googleSignInClient.signOut().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign-out failed")
        }
    }

    override suspend fun getIdToken(): Resource<String> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val token = firebaseUser.getIdToken(false).await().token
                Resource.Success(token ?: "")
            } else {
                Resource.Error("User not authenticated")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get ID token")
        }
    }

    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return User(
            userId = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: "",
            phoneNumber = firebaseUser.phoneNumber,
            profileImageUrl = firebaseUser.photoUrl?.toString(),
            isEmailVerified = firebaseUser.isEmailVerified,
            createdAt = "",
            lastActive = "",
            groupCount = 0,
            status = "ACTIVE"
        )
    }
}