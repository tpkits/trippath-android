package com.tpkits.presentation.login

import android.content.Context
import com.tpkits.data_resource.DataResource
import com.tpkits.domain.model.User
import com.tpkits.domain.usecase.GoogleLoginUsecase
import com.tpkits.presentation.base.BaseViewModel
import com.tpkits.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class LoginEvent : ViewEvent {
    data object LoginSuccess : LoginEvent()
    data class LoginError(val message: String) : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUsecase: GoogleLoginUsecase
) : BaseViewModel<LoginEvent>() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Credential Manager를 사용한 Google 로그인
     * @param activityContext Activity 컨텍스트 (Credential Manager UI 표시용)
     */
    fun signInWithGoogle(activityContext: Context) = launch {
        _errorMessage.value = null
        
        googleLoginUsecase.invoke(activityContext as Any)
            .collectDataResource(
                onSuccess = { user: User ->
                    _user.value = user
                    event(LoginEvent.LoginSuccess)
                },
                onError = { throwable: Throwable ->
                    val message = "Google 로그인 실패: ${throwable.message}"
                    _errorMessage.value = message
                    event(LoginEvent.LoginError(message))
                }
            )
    }

    /**
     * 에러 메시지를 초기화합니다.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}