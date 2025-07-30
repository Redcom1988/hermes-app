package dev.redcom1988.hermes.data.local.user.entity

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "taskId"])
data class UserTaskCrossRef(
    val userId: Int,
    val taskId: Int
)