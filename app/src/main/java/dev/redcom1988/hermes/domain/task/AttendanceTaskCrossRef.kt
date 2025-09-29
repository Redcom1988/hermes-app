package dev.redcom1988.hermes.domain.task

data class AttendanceTaskCrossRef(
    val attendanceId: Int,
    val taskId: Int,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String
)