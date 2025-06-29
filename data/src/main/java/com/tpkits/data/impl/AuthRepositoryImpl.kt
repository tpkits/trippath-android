package com.tpkits.data.impl

import android.content.Context
import com.tpkits.data.service.GoogleAuthService
import com.tpkits.domain.model.AuthResult
import com.tpkits.domain.model.User
import com.tpkits.domain.repository.AuthRepository
import com.tpkits.data.model.UserEntity
import com.tpkits.data.model.toDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleAuthService: GoogleAuthService
): AuthRepository {
    
    private var currentUser: User? = null

    override suspend fun signInWithGoogle(): AuthResult<User> {
        return try {
            val googleIdTokenCredential = googleAuthService.signInWithGoogle()
            if (googleIdTokenCredential != null) {
                // TODO: Firebase 인증 및 백엔드 서버 통신 구현
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
                AuthResult.Success(user)
            } else {
                AuthResult.Error(
                    exception = IllegalStateException("Google credential is null"),
                    message = "Google 로그인에 실패했습니다."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(
                exception = e,
                message = "Google 로그인 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    override suspend fun getCurrentUser(): User? {
        return currentUser ?: run {
            // Google 계정에서 현재 사용자 정보 확인
            val lastSignedInAccount = googleAuthService.getLastSignedInAccount()
            if (lastSignedInAccount != null) {
                // GoogleSignInAccount를 UserEntity로 변환 후 Domain으로 변환
                val userEntity = UserEntity(
                    id = lastSignedInAccount.id ?: "",
                    name = lastSignedInAccount.displayName ?: "",
                    email = lastSignedInAccount.email,
                    profileImageUrl = lastSignedInAccount.photoUrl?.toString(),
                    provider = "google",
                    isEmailVerified = true, // Google 로그인은 기본적으로 이메일이 검증됨
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                )
                val user = userEntity.toDomain()
                currentUser = user
                user
            } else {
                null
            }
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            val success = googleAuthService.signOut()
            if (success) {
                currentUser = null
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(
                    exception = RuntimeException("Logout failed"),
                    message = "로그아웃에 실패했습니다."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(
                exception = e,
                message = "로그아웃 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            val success = googleAuthService.revokeAccess()
            if (success) {
                currentUser = null
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(
                    exception = RuntimeException("Account deletion failed"),
                    message = "계정 삭제에 실패했습니다."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(
                exception = e,
                message = "계정 삭제 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }
}