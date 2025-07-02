package com.tpkits.domain.usecase

import com.tpkits.domain.model.User
import com.tpkits.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Google 로그인 관련 UseCase
 */
class GoogleLoginUsecase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    /**
     * Credential Manager를 사용한 Google 로그인
     * @param activityContext Activity 컨텍스트 (Credential Manager UI 표시용)
     * @return 로그인 결과 (성공 시 User, 실패 시 Error)
     */
    operator fun invoke(activityContext: Any) =
        authRepository.signInWithGoogle(activityContext)
}