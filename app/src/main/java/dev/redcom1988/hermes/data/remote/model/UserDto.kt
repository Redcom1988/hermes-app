package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val role: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun UserDto.toDomain() = User(
    id = id,
    email = email,
    role = role,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)