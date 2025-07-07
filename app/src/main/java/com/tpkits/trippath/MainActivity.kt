package com.tpkits.trippath

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tpkits.domain.model.User
import com.tpkits.presentation.login.LoginScreen
import com.tpkits.presentation.base.BaseScreen
import com.tpkits.trippath.ui.theme.TrippathTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrippathTheme {
                TripPathApp()
            }
        }
    }
}

@Composable
fun TripPathApp() {
    var currentUser by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (currentUser == null) {
            // 로그인하지 않은 상태: 인증 화면 표시
            LoginScreen(
                modifier = Modifier.padding(innerPadding),
                onLoginSuccess = { user ->
                    currentUser = user
                    Toast.makeText(context, "${user.name}님, 환영합니다!", Toast.LENGTH_SHORT).show()
                },
                onLoginError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            // 로그인된 상태: 메인 화면 표시
            BaseScreen(
                user = currentUser!!,
                modifier = Modifier.padding(innerPadding),
                onLogout = {
                    currentUser = null
                    Toast.makeText(context, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}