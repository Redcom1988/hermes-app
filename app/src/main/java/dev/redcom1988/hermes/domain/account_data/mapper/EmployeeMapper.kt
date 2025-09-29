package dev.redcom1988.hermes.domain.account_data.mapper

import dev.redcom1988.hermes.data.remote.model.EmployeeDto
import dev.redcom1988.hermes.data.remote.model.EmployeeTaskDto
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef

fun Employee.toDto(): EmployeeDto {
    return EmployeeDto(
        id = id,
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
}

fun EmployeeTaskCrossRef.toDto(): EmployeeTaskDto {
    return EmployeeTaskDto(
        employeeId = employeeId,
        taskId = taskId,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}