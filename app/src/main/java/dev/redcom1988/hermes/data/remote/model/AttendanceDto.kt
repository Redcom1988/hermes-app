package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.common.WorkLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto(
    val id: Int,
    @SerialName("employee_id")
    val employeeId: Int,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("end_time")
    val endTime: String?,
    @SerialName("work_location")
    val workLocation: WorkLocation,
    val longitude: Double,
    val latitude: Double,
    @SerialName("image_path")
    val imagePath: String?,
    @SerialName("task_link")
    val taskLink: String?,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun AttendanceDto.toDomain(): Attendance {
    return Attendance(
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