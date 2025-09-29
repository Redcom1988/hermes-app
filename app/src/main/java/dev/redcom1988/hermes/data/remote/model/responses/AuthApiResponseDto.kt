package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.LoginDataDto
import dev.redcom1988.hermes.data.remote.model.TokenDataDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val success: Boolean,
    val message: String,
    val data: LoginDataDto? = null
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