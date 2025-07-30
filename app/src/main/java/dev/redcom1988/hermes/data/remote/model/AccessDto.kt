package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.access.Access
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessDto (
    val id: Int,
    val name: String,
    val description: String,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun AccessDto.toDomain(): Access {
    return Access(
        id = this.id,
        name = this.name,
        description = this.description,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}