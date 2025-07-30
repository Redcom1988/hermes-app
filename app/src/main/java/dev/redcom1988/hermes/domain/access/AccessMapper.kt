package dev.redcom1988.hermes.domain.access

import dev.redcom1988.hermes.data.remote.model.AccessDto

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