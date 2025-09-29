package dev.redcom1988.hermes.domain.account_data.mapper

import dev.redcom1988.hermes.data.remote.model.EmployeeTaskDto
import dev.redcom1988.hermes.data.remote.model.UserDto
import dev.redcom1988.hermes.domain.account_data.model.User
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        role = role,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}