package com.tpkits.data.service

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.tpkits.data.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Google 인증 서비스 클래스
 * Credential Manager API와 기존 Google Sign-In API를 통합하여 관리
 */
@Singleton
class GoogleAuthService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)
    
    // Google Web Client ID (BuildConfig에서 읽되, 빈 값일 경우 fallback 사용)
    private val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID.takeIf { it.isNotEmpty() }
        ?: ""
    
    // 기존 Google Sign-In Client (로그아웃, 현재 사용자 확인용)
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId)
            .requestEmail()
            .requestProfile()
            .build()
        
        GoogleSignIn.getClient(context, gso)
    }
    
    /**
     * Credential Manager를 사용한 Google 로그인
     * @param activityContext Activity context (Credential Manager 요구사항)
     * @return Google ID Token Credential 또는 null
     */
    suspend fun signInWithGoogle(activityContext: Context): GoogleIdTokenCredential? {
        return try {
            // Generate nonce for security
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it ->
                str + "%02x".format(it)
            }
            
            // Create Google Sign-In option
            val googleIdOption = GetSignInWithGoogleOption.Builder(googleWebClientId)
                .setNonce(hashedNonce)
                .build()
            
            // Create credential request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            // Get credential
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            
            handleSignInResult(result)
        } catch (e: GetCredentialException) {
            throw e
        }
    }
    
    /**
     * 현재 로그인된 Google 계정을 반환합니다.
     * @return Google 계정 정보 또는 null
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
    
    /**
     * Google 로그아웃을 수행합니다.
     * @return 성공 여부
     */
    suspend fun signOut(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            googleSignInClient.signOut()
                .addOnCompleteListener { task ->
                    continuation.resume(task.isSuccessful)
                }
        }
    }
    
    /**
     * Google 계정 연결을 해제합니다.
     * @return 성공 여부
     */
    suspend fun revokeAccess(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            googleSignInClient.revokeAccess()
                .addOnCompleteListener { task ->
                    continuation.resume(task.isSuccessful)
                }
        }
    }
    
    /**
     * Google 로그인 인텐트를 반환합니다. (필요시 사용)
     * @return Google Sign-In Intent
     */
    fun getSignInIntent() = googleSignInClient.signInIntent
    
    /**
     * Credential Manager 결과를 처리합니다.
     */
    private fun handleSignInResult(result: GetCredentialResponse): GoogleIdTokenCredential? {
        val credential = result.credential
        
        return when {
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                try {
                    GoogleIdTokenCredential.createFrom(credential.data)
                } catch (e: GoogleIdTokenParsingException) {
                    throw e
                }
            }
            else -> null
        }
    }
} 