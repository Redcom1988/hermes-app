package dev.redcom1988.hermes.data.local.account_data.mapper

import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeTaskCrossRefEntity
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef

fun EmployeeEntity.toDomain() = Employee(
    id = employeeId,
    userId = userId,
    divisionId = divisionId,
    fullName = fullName,
    phoneNumber = phoneNumber,
    gender = gender,
    birthDate = birthDate,
    address = address,
    imagePath = imagePath,
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
    imagePath = imagePath,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun EmployeeTaskCrossRef.toEntity() = EmployeeTaskCrossRefEntity(
    employeeId = employeeId,
    taskId = taskId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
    isSynced = true
)

fun EmployeeTaskCrossRefEntity.toDomain() = EmployeeTaskCrossRef(
    employeeId = employeeId,
    taskId = taskId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)