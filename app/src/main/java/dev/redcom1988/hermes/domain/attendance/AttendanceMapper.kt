package dev.redcom1988.hermes.domain.attendance

import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.AttendanceTaskDto
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef

fun Attendance.toDto(): AttendanceDto {
    return AttendanceDto(
        id = this.id,
        employeeId = this.employeeId,
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

fun AttendanceTaskCrossRef.toDto(): AttendanceTaskDto {
    return AttendanceTaskDto(
        attendanceId = this.attendanceId,
        taskId = this.taskId,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}