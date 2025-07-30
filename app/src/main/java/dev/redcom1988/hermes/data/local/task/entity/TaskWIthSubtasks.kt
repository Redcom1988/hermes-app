package dev.redcom1988.hermes.data.local.task.entity

import androidx.room.Embedded
import androidx.room.Relation
import dev.redcom1988.hermes.domain.common.SyncStatus

data class TaskWithSubTasks(
    @Embedded val task: TaskEntity,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "parentTaskId"
    )
    val subTasks: List<TaskEntity>
)

val TaskWithSubTasks.visibleSubTasks: List<TaskEntity>
    get() = subTasks.filter { !it.isDeleted && it.syncStatus != SyncStatus.DELETED }