package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.AttendanceTaskDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceApiResponseDto(
    val attendances: List<AttendanceDto>?,
    @SerialName("attendance_tasks")
    val attendanceTasks: List<AttendanceTaskDto>?
)

fun AttendanceApiResponseDto.toDomainAttendances(): List<Attendance> {
    return attendances?.map { it.toDomain() } ?: emptyList()
}

fun AttendanceApiResponseDto.toDomainAttendanceTasks(): List<AttendanceTaskCrossRef> {
    return attendanceTasks?.map { it.toDomain() } ?: emptyList()
}