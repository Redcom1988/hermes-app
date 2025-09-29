package dev.redcom1988.hermes.data.local.attendance.entity

import androidx.room.Entity
import androidx.room.Index
import dev.redcom1988.hermes.core.util.extension.formatToString
import java.time.LocalDateTime

@Entity(
    tableName = "attendance_tasks",
    primaryKeys = ["attendanceId", "taskId"],
    indices = [
        Index(value = ["attendanceId"]),
        Index(value = ["taskId"])
    ]
)
data class AttendanceTaskCrossRefEntity(
    val attendanceId: Int,
    val taskId: Int,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString()
)