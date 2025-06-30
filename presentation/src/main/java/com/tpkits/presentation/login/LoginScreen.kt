package com.tpkits.presentation.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.tpkits.domain.model.User
import kotlinx.coroutines.launch

/**
 * 로그인 화면
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (User) -> Unit = {},
    onLoginError: (String) -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // ViewModel 상태 관찰
    LaunchedEffect(viewModel.user) {
        viewModel.user?.let { user ->
            onLoginSuccess(user)
        }
    }
    
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            onLoginError(error)
            viewModel.clearError()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 앱 제목
        Text(
            text = "TripPath",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 설명 텍스트
        Text(
            text = "여행의 새로운 경험을 시작하세요",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Google 로그인 버튼
        GoogleSignInButton(
            onClick = {
                if (!viewModel.isLoading) {
                    coroutineScope.launch {
                        try {
                            val activity = context as Activity
                            viewModel.signInWithGoogle(activity)
                        } catch (e: GetCredentialCancellationException) {
                            onLoginError("Google 로그인이 취소되었습니다.")
                        } catch (e: NoCredentialException) {
                            onLoginError("사용 가능한 Google 계정이 없습니다.")
                        } catch (e: GetCredentialException) {
                            onLoginError("Google 로그인 실패: ${e.message}")
                        } catch (e: Exception) {
                            onLoginError("로그인 중 오류가 발생했습니다: ${e.message}")
                        }
                    }
                }
            },
            enabled = !viewModel.isLoading,
            text = if (viewModel.isLoading) "로그인 중..." else "Google로 로그인"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 이용약관 및 개인정보처리방침 텍스트
        Text(
            text = "로그인하면 이용약관 및 개인정보처리방침에 동의하는 것으로 간주됩니다.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

/**
 * Google 로그인 버튼 컴포넌트
 */
@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "Google로 로그인"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Google 로고 (실제로는 리소스에서 가져와야 함)
            // 임시로 'G' 텍스트 사용
            Text(
                text = "G",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4) // Google Blue
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}