package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.user.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val role: String,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun UserDto.toDomain() = dev.redcom1988.hermes.domain.user.User(
    id = id,
    email = email,
    role = UserRole.fromLabel(role) ?: UserRole.USER,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)