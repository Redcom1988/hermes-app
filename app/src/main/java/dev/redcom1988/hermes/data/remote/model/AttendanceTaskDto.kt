package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceTaskDto(
    @SerialName("attendance_id")
    val attendanceId: Int,
    @SerialName("task_id")
    val taskId: Int,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String,
)

fun AttendanceTaskDto.toDomain() = AttendanceTaskCrossRef(
    attendanceId = attendanceId,
    taskId = taskId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)