package com.tpkits.domain.usecase

import com.tpkits.domain.model.User
import com.tpkits.domain.repository.AuthRepository

class GetCurrentUserUsecase(private val authRepository: AuthRepository) {

    /**
     * 현재 로그인된 사용자 정보를 가져옵니다.
     * @return 현재 사용자 정보 또는 null (로그인되지 않은 경우)
     */
    suspend fun execute(): User? {
        return authRepository.getCurrentUser()
    }
} 