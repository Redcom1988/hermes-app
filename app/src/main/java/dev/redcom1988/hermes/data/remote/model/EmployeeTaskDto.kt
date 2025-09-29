package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeTaskDto(
    @SerialName("employee_id")
    val employeeId: Int,
    @SerialName("task_id")
    val taskId: Int,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun EmployeeTaskDto.toDomain() = EmployeeTaskCrossRef(
    employeeId = employeeId,
    taskId = taskId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)