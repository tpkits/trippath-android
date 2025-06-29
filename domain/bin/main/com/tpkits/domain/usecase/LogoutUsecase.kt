package com.tpkits.domain.usecase

import com.tpkits.domain.model.AuthResult
import com.tpkits.domain.repository.AuthRepository

class LogoutUsecase(private val authRepository: AuthRepository) {

    /**
     * 로그아웃을 수행합니다.
     * @return 로그아웃 결과
     */
    suspend fun execute(): AuthResult<Unit> {
        return try {
            authRepository.logout()
        } catch (e: Exception) {
            AuthResult.Error(
                exception = e,
                message = "로그아웃 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }
} 