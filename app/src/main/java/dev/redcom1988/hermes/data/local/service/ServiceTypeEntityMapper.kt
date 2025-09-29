package dev.redcom1988.hermes.data.local.service

import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeEntity
import dev.redcom1988.hermes.domain.service.ServiceType

fun ServiceTypeEntity.toDomain() = ServiceType(
    id = serviceTypeId,
    name = name,
    description = description,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun ServiceType.toEntity() = ServiceTypeEntity(
    serviceTypeId = id,
    name = name,
    description = description,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)