package dev.redcom1988.hermes.domain.account_data.mapper

import dev.redcom1988.hermes.data.remote.model.AccessDto
import dev.redcom1988.hermes.domain.account_data.model.Access

fun Access.toDto(): AccessDto {
    return AccessDto(
        id = this.id,
        name = this.name,
        description = this.description,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}