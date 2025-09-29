package dev.redcom1988.hermes.data.local.attendance

import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.domain.attendance.Attendance

fun AttendanceEntity.toDomain() = Attendance(
    id = attendanceId,
    employeeId = employeeId,
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

fun Attendance.toEntity(isSynced: Boolean = true) = AttendanceEntity(
    attendanceId = id,
    employeeId = employeeId,
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
    isSynced = isSynced,
)