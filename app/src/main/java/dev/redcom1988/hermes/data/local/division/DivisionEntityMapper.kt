package dev.redcom1988.hermes.data.local.division

import dev.redcom1988.hermes.data.local.division.entity.DivisionEntity
import dev.redcom1988.hermes.domain.division.Division

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