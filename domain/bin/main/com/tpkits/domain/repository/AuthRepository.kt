package com.tpkits.domain.repository

import com.tpkits.domain.model.AuthResult
import com.tpkits.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /**
     * Credential Manager를 사용한 Google 로그인
     * @param activityContext Activity 컨텍스트 (Credential Manager UI 표시용)
     * @return 로그인 결과 (성공 시 User, 실패 시 Error)
     */
    suspend fun signInWithGoogle(activityContext: Any): AuthResult<User>
    
    /**
     * 현재 로그인된 사용자 정보를 가져옵니다.
     * @return 사용자 정보 또는 null
     */
    suspend fun getCurrentUser(): User?
    
    /**
     * 로그아웃을 수행합니다.
     */
    suspend fun logout(): AuthResult<Unit>
    
    /**
     * 회원탈퇴를 수행합니다.
     */
    suspend fun deleteAccount(): AuthResult<Unit>
    
    /**
     * 액세스 토큰과 리프레시 토큰을 저장합니다.
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    
    /**
     * 액세스 토큰을 가져옵니다.
     */
    suspend fun getAccessToken(): String?
    
    /**
     * 리프레시 토큰을 가져옵니다.
     */
    suspend fun getRefreshToken(): String?
    
    /**
     * 저장된 토큰들을 삭제합니다.
     */
    suspend fun clearTokens()
    
    /**
     * 토큰이 유효한지 확인합니다.
     */
    fun isTokenValid(): Flow<Boolean>
}