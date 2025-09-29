package dev.redcom1988.hermes.domain.account_data.model

class EmployeeTaskCrossRef(
    val employeeId: Int,
    val taskId: Int,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String,
)