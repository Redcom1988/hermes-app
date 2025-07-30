package dev.redcom1988.hermes.data.local.employee

import dev.redcom1988.hermes.data.local.employee.entity.EmployeeEntity
import dev.redcom1988.hermes.domain.employee.Employee

fun EmployeeEntity.toDomain() = Employee(
    id = employeeId,
    userId = userId,
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

fun Employee.toEntity() = EmployeeEntity(
    employeeId = id,
    userId = userId,
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