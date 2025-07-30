package dev.redcom1988.hermes.data.local.access

import dev.redcom1988.hermes.data.local.access.entity.AccessEntity
import dev.redcom1988.hermes.domain.access.Access

fun AccessEntity.toDomain() = Access(
    id = accessId,
    name = name,
    description = description,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)

fun Access.toEntity() = AccessEntity(
    accessId = id,
    name = name,
    description = description,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)