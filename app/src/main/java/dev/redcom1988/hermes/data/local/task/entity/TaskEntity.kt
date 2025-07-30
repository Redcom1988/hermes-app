package dev.redcom1988.hermes.data.local.task.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.domain.common.SyncStatus
import dev.redcom1988.hermes.domain.task.TaskStatus
import java.time.LocalDateTime

const val taskEntityTableName = "tasks"

@Entity(tableName = taskEntityTableName)
data class TaskEntity(
    @PrimaryKey val taskId: Int,
    val taskName: String,
    val taskDescription: String? = null,
    val deadline: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val parentTaskId: Int? = null,
    val note: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
    val syncStatus: SyncStatus = SyncStatus.CREATED
)