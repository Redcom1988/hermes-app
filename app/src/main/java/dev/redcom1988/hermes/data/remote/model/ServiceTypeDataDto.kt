package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.service.ServiceTypeDataCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceTypeDataDto(
    @SerialName("field_id")
    val fieldId: Int,
    @SerialName("service_id")
    val serviceId: Int,
    val value: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun ServiceTypeDataDto.toDomain() = ServiceTypeDataCrossRef(
    fieldId = fieldId,
    serviceId = serviceId,
    value = value,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)