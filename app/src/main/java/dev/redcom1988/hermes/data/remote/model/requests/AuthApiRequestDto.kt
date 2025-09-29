package dev.redcom1988.hermes.data.remote.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)