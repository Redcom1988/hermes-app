package dev.redcom1988.hermes.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDataDto(
    val user: AuthUserDto,
    val employee: AuthEmployeeDto?,
    val division: AuthDivisionDto?,
    val token: String,
    @SerialName("expires_at")
    val expiresAt: String
)

@Serializable
data class AuthUserDto(
    val id: Int,
    val email: String,
    val role: String // Single role as string instead of array
)

@Serializable
data class AuthEmployeeDto(
    @SerialName("full_name")
    val name: String,
    val id: Int
)

@Serializable
data class AuthDivisionDto(
    val name: String
)

@Serializable
data class TokenDataDto(
    val token: String,
    @SerialName("expires_at")
    val expiresAt: String
)