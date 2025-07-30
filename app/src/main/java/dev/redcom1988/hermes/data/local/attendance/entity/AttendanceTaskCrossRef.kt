package dev.redcom1988.hermes.data.local.attendance.entity

import androidx.room.Entity

@Entity(primaryKeys = ["attendanceId", "taskId"])
data class AttendanceTaskCrossRef(
    val attendanceId: Int,
    val taskId: Int
)