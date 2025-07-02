package com.tpkits.domain.repository

import com.tpkits.data_resource.DataResource
import com.tpkits.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /**
     * Credential Manager를 사용한 Google 로그인
     * @param activityContext Activity 컨텍스트 (Credential Manager UI 표시용)
     * @return 로그인 결과 (성공 시 User, 실패 시 Error)
     */
    fun signInWithGoogle(activityContext: Any): Flow<DataResource<User>>
    
    /**
     * 현재 로그인된 사용자 정보를 가져옵니다.
     */
    fun getCurrentUser(): Flow<DataResource<User>>
    
    /**
     * 로그아웃을 수행합니다.
     */
    fun logout(): Flow<DataResource<Unit>>
    
    /**
     * 회원탈퇴를 수행합니다.
     */
    fun deleteAccount(): Flow<DataResource<Unit>>
    
    /**
     * 액세스 토큰과 리프레시 토큰을 저장합니다.
     */
    fun saveTokens(accessToken: String, refreshToken: String)
    
    /**
     * 액세스 토큰을 가져옵니다.
     */
    fun getAccessToken(): Flow<DataResource<String>>
    
    /**
     * 리프레시 토큰을 가져옵니다.
     */
    fun getRefreshToken(): Flow<DataResource<String>>
    
    /**
     * 저장된 토큰들을 삭제합니다.
     */
    fun clearTokens()
    
    /**
     * 토큰이 유효한지 확인합니다.
     */
    fun isTokenValid(): Flow<DataResource<Boolean>>
}