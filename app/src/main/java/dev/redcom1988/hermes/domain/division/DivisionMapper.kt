package dev.redcom1988.hermes.domain.division

import dev.redcom1988.hermes.data.remote.model.DivisionDto

fun Division.toDto(): DivisionDto {
    return DivisionDto(
        id = this.id,
        name = this.name,
        requiredWorkHours = this.requiredWorkHours,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}