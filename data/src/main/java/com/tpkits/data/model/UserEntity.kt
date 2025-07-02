package com.tpkits.data.model

import com.tpkits.data.DataMapper
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
) : DataMapper<User> {
    override fun toDomain(): User = User(
        id,
        name,
        email,
        profileImageUrl,
        provider?.let { 
            try {
                SnsType.valueOf(it.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        },
        isEmailVerified = isEmailVerified,
        createdAt,
        lastLoginAt
    )
}