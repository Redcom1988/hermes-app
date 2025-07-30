package dev.redcom1988.hermes.data.local.user.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity

data class UserWithTasks(
    @Embedded val user: UserEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "taskId",
        associateBy = Junction(UserTaskCrossRef::class)
    )
    val tasks: List<TaskEntity>
)
