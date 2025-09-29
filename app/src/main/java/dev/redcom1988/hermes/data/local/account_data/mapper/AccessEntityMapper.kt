package dev.redcom1988.hermes.data.local.account_data.mapper

import dev.redcom1988.hermes.data.local.account_data.entity.AccessEntity
import dev.redcom1988.hermes.domain.account_data.model.Access

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