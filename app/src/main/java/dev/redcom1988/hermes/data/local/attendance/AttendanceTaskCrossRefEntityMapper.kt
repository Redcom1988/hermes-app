package dev.redcom1988.hermes.data.local.attendance

import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceTaskCrossRefEntity
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef

fun AttendanceTaskCrossRefEntity.toDomain() = AttendanceTaskCrossRef(
    attendanceId = attendanceId,
    taskId = taskId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun AttendanceTaskCrossRef.toEntity() = AttendanceTaskCrossRefEntity(
    attendanceId = attendanceId,
    taskId = taskId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
    isSynced = true
)