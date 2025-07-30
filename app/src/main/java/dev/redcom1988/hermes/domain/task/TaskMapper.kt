package dev.redcom1988.hermes.domain.task

import dev.redcom1988.hermes.data.remote.model.TaskDto

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        name = name,
        description = description,
        deadline = deadline,
        status = status.label,
        parentTaskId = parentTaskId,
        note = note,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}