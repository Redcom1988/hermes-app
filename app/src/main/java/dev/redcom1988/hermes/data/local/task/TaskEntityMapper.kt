package dev.redcom1988.hermes.data.local.task

import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.domain.common.SyncStatus
import dev.redcom1988.hermes.domain.task.Task

fun TaskEntity.toDomain() = Task(
    id = taskId,
    name = taskName,
    description = taskDescription,
    deadline = deadline,
    status = status,
    parentTaskId = parentTaskId,
    note = note,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun Task.toEntity() = TaskEntity(
    taskId = id,
    taskName = name,
    taskDescription = description,
    deadline = deadline,
    status = status,
    parentTaskId = parentTaskId,
    note = note,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
    syncStatus = SyncStatus.UNCHANGED
)