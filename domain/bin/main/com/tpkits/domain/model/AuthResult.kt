package com.tpkits.domain.model

/**
 * 인증 관련 작업의 결과를 나타내는 sealed class
 */
sealed class AuthResult<out T> {
    /**
     * 성공 결과
     * @param data 성공 시 반환되는 데이터
     */
    data class Success<T>(val data: T) : AuthResult<T>()
    
    /**
     * 실패 결과
     * @param exception 발생한 예외
     * @param message 사용자에게 표시할 에러 메시지
     */
    data class Error(
        val exception: Throwable, 
        val message: String = exception.message ?: "Unknown error"
    ) : AuthResult<Nothing>()
} 