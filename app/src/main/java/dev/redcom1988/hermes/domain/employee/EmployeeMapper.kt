package dev.redcom1988.hermes.domain.employee

import dev.redcom1988.hermes.data.remote.model.EmployeeDto

fun Employee.toDto(): EmployeeDto {
    return EmployeeDto(
        id = id,
        divisionId = divisionId,
        fullName = fullName,
        phoneNumber = phoneNumber,
        gender = gender,
        birthDate = birthDate,
        address = address,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}