package com.tpkits.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tpkits.domain.model.AuthResult
import com.tpkits.domain.model.User
import com.tpkits.domain.usecase.GoogleLoginUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUsecase: GoogleLoginUsecase
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var user by mutableStateOf<User?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Credential Manager를 사용한 Google 로그인
     */
    suspend fun signInWithGoogle() {
        isLoading = true
        errorMessage = null
        
        try {
            when (val result = googleLoginUsecase.signInWithCredentialManager()) {
                is AuthResult.Success -> {
                    user = result.data
                }
                is AuthResult.Error -> {
                    errorMessage = result.message
                }
            }
        } catch (e: Exception) {
            errorMessage = "Google 로그인 실패: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    /**
     * 에러 메시지를 초기화합니다.
     */
    fun clearError() {
        errorMessage = null
    }
}