package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val deadline: String,
    val status: String,
    @SerialName("parent_task_id")
    val parentTaskId: Int? = null,
    val note: String? = null,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun TaskDto.toDomain() = Task(
    id = id,
    name = name,
    description = description,
    deadline = deadline,
    status = TaskStatus.fromLabel(status) ?: TaskStatus.PENDING,
    parentTaskId = parentTaskId,
    note = note,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)