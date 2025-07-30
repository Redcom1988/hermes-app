package dev.redcom1988.hermes.data.local.attendance

import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.common.SyncStatus

fun AttendanceEntity.toDomain() = Attendance(
    id = attendanceId,
    userId = userId,
    startTime = startTime,
    endTime = endTime,
    workLocation = workLocation,
    longitude = longitude,
    latitude = latitude,
    imagePath = imagePath,
    taskLink = taskLink,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun Attendance.toEntity() = AttendanceEntity(
    attendanceId = id,
    userId = userId,
    startTime = startTime,
    endTime = endTime,
    workLocation = workLocation,
    longitude = longitude,
    latitude = latitude,
    imagePath = imagePath,
    taskLink = taskLink,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
    syncStatus = SyncStatus.UNCHANGED,
)