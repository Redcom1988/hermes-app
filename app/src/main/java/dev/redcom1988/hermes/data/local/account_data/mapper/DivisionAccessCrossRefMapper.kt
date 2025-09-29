package dev.redcom1988.hermes.data.local.account_data.mapper

import dev.redcom1988.hermes.data.local.account_data.entity.DivisionAccessCrossRefEntity
import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef

fun DivisionAccessCrossRefEntity.toDomain() = DivisionAccessCrossRef(
    divisionId = divisionId,
    accessId = accessId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun DivisionAccessCrossRef.toEntity() = DivisionAccessCrossRefEntity(
    divisionId = divisionId,
    accessId = accessId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)