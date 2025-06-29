package com.tpkits.domain.model

/**
 * 사용자 정보를 나타내는 데이터 클래스
 */
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val snsType: SnsType? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long? = null,
    val lastLoginAt: Long? = null
)
