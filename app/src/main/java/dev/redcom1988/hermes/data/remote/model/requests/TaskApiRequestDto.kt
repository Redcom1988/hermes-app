package dev.redcom1988.hermes.data.remote.model.requests

import dev.redcom1988.hermes.data.remote.model.EmployeeTaskDto
import dev.redcom1988.hermes.data.remote.model.TaskDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskApiRequestDto(
    val tasks: List<TaskDto>? = emptyList(),
    @SerialName("employee_tasks")
    val employeeTasks: List<EmployeeTaskDto>? = emptyList()
)