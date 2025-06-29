package com.tpkits.domain.repository

import com.tpkits.domain.model.AuthResult
import com.tpkits.domain.model.SnsType
import com.tpkits.domain.model.User

interface AuthRepository {

    /**
     * Credential Manager를 사용한 Google 로그인
     * @return 로그인 결과 (성공 시 User, 실패 시 Error)
     */
    suspend fun signInWithGoogle(): AuthResult<User>
    
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
}