package com.tpkits.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_tokens")

interface TokenManager {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
    fun isTokenValid(): Flow<Boolean>
}

@Singleton
class TokenManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenManager {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
    
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = encryptData(accessToken)
            preferences[REFRESH_TOKEN_KEY] = encryptData(refreshToken)
        }
    }
    
    override suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]?.let { encryptedToken ->
                decryptData(encryptedToken)
            }
        }.first()
    }
    
    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]?.let { encryptedToken ->
                decryptData(encryptedToken)
            }
        }.first()
    }
    
    override suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
    
    override fun isTokenValid(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null && preferences[REFRESH_TOKEN_KEY] != null
        }
    }
    
    private fun encryptData(data: String): String {
        // 1. 임시 파일 생성
        val file = File(context.cacheDir, "temp_encrypt_${System.currentTimeMillis()}")
        // 2. EncryptedFile 인스턴스 생성
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        // 3. 임시 파일에 데이터 쓰기 (이 때 암호화 진행)
        encryptedFile.openFileOutput().use { output ->
            output.write(data.toByteArray())
        }
        // 4. 암호화된 파일의 내용을 바이트 배열로 읽기
        val encryptedData = file.readBytes()
        // 5. 임시 파일 삭제
        file.delete()
        // 6. Base64로 인코딩하여 문자열 반환
        return android.util.Base64.encodeToString(encryptedData, android.util.Base64.DEFAULT)
    }
    
    private fun decryptData(encryptedData: String): String {
        val file = File(context.cacheDir, "temp_decrypt_${System.currentTimeMillis()}")
        file.writeBytes(android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT))
        
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        
        val decryptedData = encryptedFile.openFileInput().use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        }
        
        file.delete()
        return decryptedData
    }
}