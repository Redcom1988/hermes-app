package dev.redcom1988.hermes.data.local.service

import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeDataCrossRefEntity
import dev.redcom1988.hermes.domain.service.ServiceTypeDataCrossRef

fun ServiceTypeDataCrossRefEntity.toDomain() = ServiceTypeDataCrossRef(
    fieldId = fieldId,
    serviceId = serviceId,
    value = value,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun ServiceTypeDataCrossRef.toEntity(isSynced: Boolean = true) = ServiceTypeDataCrossRefEntity(
    fieldId = fieldId,
    serviceId = serviceId,
    value = value,
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)