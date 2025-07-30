package dev.redcom1988.hermes.domain.attendance

import dev.redcom1988.hermes.data.remote.model.AttendanceDto

fun Attendance.toDto(): AttendanceDto {
    return AttendanceDto(
        id = this.id,
        userId = this.userId,
        startTime = this.startTime,
        endTime = this.endTime,
        workLocation = this.workLocation,
        longitude = this.longitude,
        latitude = this.latitude,
        imagePath = this.imagePath,
        taskLink = this.taskLink,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}