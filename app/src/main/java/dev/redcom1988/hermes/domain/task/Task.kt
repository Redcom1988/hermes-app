package dev.redcom1988.hermes.domain.task

import dev.redcom1988.hermes.data.local.task.entity.TaskWithSubTasks

class Task (
    val id: Int,
    val name: String,
    val description: String?,
    val deadline: String,
    val status: TaskStatus,
    val parentTaskId: Int? = null,
    val note: String? = null,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String,
)