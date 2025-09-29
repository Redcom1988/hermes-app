package dev.redcom1988.hermes.data.local.service

import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeFieldEntity
import dev.redcom1988.hermes.domain.service.ServiceTypeField

fun ServiceTypeFieldEntity.toDomain() = ServiceTypeField(
    id = fieldId,
    serviceTypeId = serviceTypeId,
    fieldName = fieldName,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun ServiceTypeField.toEntity() = ServiceTypeFieldEntity(
    fieldId = id,
    serviceTypeId = serviceTypeId,
    fieldName = fieldName,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)