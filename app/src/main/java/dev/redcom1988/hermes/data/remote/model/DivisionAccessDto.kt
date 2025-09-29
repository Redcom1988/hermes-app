package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DivisionAccessDto(
    @SerialName("access_id")
    val accessId: Int,
    @SerialName("division_id")
    val divisionId: Int,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun DivisionAccessDto.toDomain() = DivisionAccessCrossRef(
    accessId = accessId,
    divisionId = divisionId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)