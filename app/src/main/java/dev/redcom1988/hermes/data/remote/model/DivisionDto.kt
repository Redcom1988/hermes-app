package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.division.Division
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DivisionDto (
    val id: Int,
    val name: String,
    val requiredWorkHours: Int,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun DivisionDto.toDomain() = Division(
    id = id,
    name = name,
    requiredWorkHours = requiredWorkHours,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)