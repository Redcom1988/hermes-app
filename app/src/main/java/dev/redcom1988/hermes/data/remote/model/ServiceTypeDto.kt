package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.service.ServiceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceTypeDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun ServiceTypeDto.toDomain() = ServiceType(
    id = id,
    name = name,
    description = description,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)