package dev.redcom1988.hermes.domain.user

import dev.redcom1988.hermes.data.remote.model.UserDto

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        role = role.label,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}