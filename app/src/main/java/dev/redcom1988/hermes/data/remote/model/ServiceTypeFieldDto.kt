package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.service.ServiceTypeField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceTypeFieldDto(
    val id: Int,
    @SerialName("service_type_id")
    val serviceTypeId: Int,
    @SerialName("field_name")
    val fieldName: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun ServiceTypeFieldDto.toDomain() = ServiceTypeField(
    id = id,
    serviceTypeId = serviceTypeId,
    fieldName = fieldName,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)