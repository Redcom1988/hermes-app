package dev.redcom1988.hermes.data.local.account_data.mapper

import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import dev.redcom1988.hermes.domain.account_data.model.User
import dev.redcom1988.hermes.domain.account_data.enums.UserRole

fun UserEntity.toDomain() = User(
    id = userId,
    email = email,
    role = role.label,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun User.toEntity() = UserEntity(
    userId = id,
    email = email,
    role = UserRole.fromLabel(role) ?: UserRole.USER,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)