package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.account_data.model.Division
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DivisionDto(
    val id: Int,
    val name: String,
    @SerialName("required_workhours")
    val requiredWorkHours: Int,
    @SerialName("is_deleted")
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