package dev.redcom1988.hermes.data.local.task.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.redcom1988.hermes.data.local.user.entity.UserEntity
import dev.redcom1988.hermes.data.local.user.entity.UserTaskCrossRef

data class TaskWithUsers(
    @Embedded val task: TaskEntity,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "userId",
        associateBy = Junction(UserTaskCrossRef::class)
    )
    val users: List<UserEntity>
)
