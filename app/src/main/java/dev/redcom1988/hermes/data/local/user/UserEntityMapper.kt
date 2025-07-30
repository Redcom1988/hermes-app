package dev.redcom1988.hermes.data.local.user

import dev.redcom1988.hermes.data.local.user.entity.UserEntity
import dev.redcom1988.hermes.domain.user.User

fun UserEntity.toDomain() = User(
    id = userId,
    email = email,
    role = role,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun User.toEntity() = UserEntity(
    userId = id,
    email = email,
    role = role,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)