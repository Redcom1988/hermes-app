package dev.redcom1988.hermes.data.remote.model.requests

import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.AttendanceTaskDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceApiRequestDto(
    val attendances: List<AttendanceDto>? = emptyList(),
    @SerialName("attendance_tasks")
    val attendanceTasks: List<AttendanceTaskDto>? = emptyList()
)