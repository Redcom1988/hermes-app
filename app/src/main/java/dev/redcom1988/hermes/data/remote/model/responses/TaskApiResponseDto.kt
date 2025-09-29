package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.EmployeeTaskDto
import dev.redcom1988.hermes.data.remote.model.TaskDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
import dev.redcom1988.hermes.domain.task.Task
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskApiResponseDto(
    val tasks: List<TaskDto>?,
    @SerialName("employee_tasks")
    val employeeTasks: List<EmployeeTaskDto>?,
)

fun TaskApiResponseDto.toDomainTasks(): List<Task> {
    return tasks?.map { it.toDomain() } ?: emptyList()
}

fun TaskApiResponseDto.toDomainEmployeeTasks(): List<EmployeeTaskCrossRef> {
    return employeeTasks?.map { it.toDomain() } ?: emptyList()
}