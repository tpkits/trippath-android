package com.tpkits.data.impl

import android.content.Context
import com.tpkits.data.local.TokenManager
import com.tpkits.data.service.GoogleAuthService
import com.tpkits.data_resource.DataResource
import com.tpkits.domain.model.User
import com.tpkits.domain.repository.AuthRepository
import com.tpkits.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val googleAuthService: GoogleAuthService,
    private val tokenManager: TokenManager,
): AuthRepository {
    
    private var currentUser: User? = null

    override fun signInWithGoogle(activityContext: Any): Flow<DataResource<User>> = flow {
        emit(DataResource.loading())
        try {
            val googleIdTokenCredential = googleAuthService.signInWithGoogle(activityContext as Context)
            if (googleIdTokenCredential != null) {
                val user = UserEntity(
                    id = googleIdTokenCredential.id,
                    name = googleIdTokenCredential.displayName ?: "",
                    email = googleIdTokenCredential.id,
                    profileImageUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    provider = "google",
                    isEmailVerified = true,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                ).toDomain()
                currentUser = user
                emit(DataResource.success(user))
            } else {
                emit(DataResource.error(IllegalStateException("Google credential is null")))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }

    override fun getCurrentUser(): Flow<DataResource<User>> = flow {
        emit(DataResource.loading())
        try {
            val user = currentUser ?: run {
                val lastSignedInAccount = googleAuthService.getLastSignedInAccount()
                if (lastSignedInAccount != null) {
                    val userFromAccount = UserEntity(
                        id = lastSignedInAccount.id ?: "",
                        name = lastSignedInAccount.displayName ?: "",
                        email = lastSignedInAccount.email,
                        profileImageUrl = lastSignedInAccount.photoUrl?.toString(),
                        provider = "google",
                        isEmailVerified = true,
                        createdAt = System.currentTimeMillis(),
                        lastLoginAt = System.currentTimeMillis()
                    ).toDomain()
                    currentUser = userFromAccount
                    userFromAccount
                } else {
                    null
                }
            }
            
            if (user != null) {
                emit(DataResource.success(user))
            } else {
                emit(DataResource.error(IllegalStateException("No user logged in")))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }

    override fun logout(): Flow<DataResource<Unit>> = flow {
        emit(DataResource.loading())
        try {
            val success = googleAuthService.signOut()
            if (success) {
                currentUser = null
                tokenManager.clearTokens()
                emit(DataResource.success(Unit))
            } else {
                emit(DataResource.error(RuntimeException("Logout failed")))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }

    override fun deleteAccount(): Flow<DataResource<Unit>> = flow {
        emit(DataResource.loading())
        try {
            val success = googleAuthService.revokeAccess()
            if (success) {
                currentUser = null
                tokenManager.clearTokens()
                emit(DataResource.success(Unit))
            } else {
                emit(DataResource.error(RuntimeException("Account deletion failed")))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
    
    override fun saveTokens(accessToken: String, refreshToken: String) {
        runBlocking {
            tokenManager.saveTokens(accessToken, refreshToken)
        }
    }
    
    override fun getAccessToken(): Flow<DataResource<String>> = flow {
        emit(DataResource.loading())
        try {
            val token = tokenManager.getAccessToken()
            if (token != null) {
                emit(DataResource.success(token))
            } else {
                emit(DataResource.error(IllegalStateException("No access token found")))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
    
    override fun getRefreshToken(): Flow<DataResource<String>> = flow {
        emit(DataResource.loading())
        try {
            val token = tokenManager.getRefreshToken()
            if (token != null) {
                emit(DataResource.success(token))
            } else {
                emit(DataResource.error(IllegalStateException("No refresh token found")))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
    
    override fun clearTokens() {
        runBlocking {
            tokenManager.clearTokens()
        }
    }
    
    override fun isTokenValid(): Flow<DataResource<Boolean>> = flow {
        emit(DataResource.loading())
        try {
            tokenManager.isTokenValid().collect { isValid ->
                emit(DataResource.success(isValid))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}