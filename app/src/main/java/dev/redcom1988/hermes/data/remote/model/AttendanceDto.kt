package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.attendance.AttendanceWorkLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto (
    val id: Int,
    val userId: Int,
    val startTime: String,
    val endTime: String?,
    val workLocation: AttendanceWorkLocation,
    val longitude: Double,
    val latitude: Double,
    val imagePath: String?,
    val taskLink: String?,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun AttendanceDto.toDomain(): Attendance {
    return Attendance(
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
