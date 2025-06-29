package com.tpkits.data.model

import com.tpkits.domain.model.SnsType
import com.tpkits.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val name: String,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val provider: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long? = null,
    val lastLoginAt: Long? = null
)

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        snsType = when (provider) {
            "google" -> SnsType.GOOGLE
            "kakao" -> SnsType.KAKAO
            "apple" -> SnsType.APPLE
            else -> null
        },
        isEmailVerified = isEmailVerified,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )
} 