package dev.redcom1988.hermes.domain.task

data class Task(
    val id: Int,
    val name: String,
    val description: String?,
    val deadline: String,
    val status: TaskStatus,
    val parentTaskId: Int? = null,
    val note: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
)