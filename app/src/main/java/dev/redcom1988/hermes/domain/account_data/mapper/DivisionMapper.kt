package dev.redcom1988.hermes.domain.account_data.mapper

import dev.redcom1988.hermes.data.remote.model.DivisionAccessDto
import dev.redcom1988.hermes.data.remote.model.DivisionDto
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef

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