package dev.redcom1988.hermes.domain.attendance

data class Attendance(
    val id: Int,
    val userId: Int,
    val startTime: String,
    val endTime: String?,
    val workLocation: AttendanceWorkLocation,
    val longitude: Double,
    val latitude: Double,
    val imagePath: String? = null,
    val taskLink: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
) {

}