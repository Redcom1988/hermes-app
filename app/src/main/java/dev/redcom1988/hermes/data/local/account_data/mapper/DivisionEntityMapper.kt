package dev.redcom1988.hermes.data.local.account_data.mapper

import dev.redcom1988.hermes.data.local.account_data.entity.DivisionEntity
import dev.redcom1988.hermes.domain.account_data.model.Division

fun DivisionEntity.toDomain() = Division(
    id = divisionId,
    name = divisionName,
    requiredWorkHours = requiredWorkHours,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)

fun Division.toEntity() = DivisionEntity(
    divisionId = id,
    divisionName = name,
    requiredWorkHours = requiredWorkHours,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)