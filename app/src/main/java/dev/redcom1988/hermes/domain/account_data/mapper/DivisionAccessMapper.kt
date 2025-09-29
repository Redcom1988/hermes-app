package dev.redcom1988.hermes.domain.account_data.mapper

import dev.redcom1988.hermes.data.remote.model.DivisionAccessDto
import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef

fun DivisionAccessCrossRef.toDto(): DivisionAccessDto {
    return DivisionAccessDto(
        accessId = this.accessId,
        divisionId = this.divisionId,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}