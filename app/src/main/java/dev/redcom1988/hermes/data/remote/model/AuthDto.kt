package dev.redcom1988.hermes.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val success: Boolean,
    val message: String,
    val data: LoginDataDto? = null
)

@Serializable
data class LoginDataDto(
    val user: UserDto,
    val token: String,
    @SerialName("expires_at")
    val expiresAt: String
)


@Serializable
data class LogoutResponseDto(
    val success: Boolean,
    val message: String
)

@Serializable
data class RefreshTokenResponseDto(
    val success: Boolean,
    val message: String,
    val data: TokenDataDto? = null
)

@Serializable
data class TokenDataDto(
    val token: String,
    @SerialName("expires_at")
    val expiresAt: String
)