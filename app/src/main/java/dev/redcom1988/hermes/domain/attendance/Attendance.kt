package dev.redcom1988.hermes.domain.attendance

import dev.redcom1988.hermes.domain.common.WorkLocation

data class Attendance(
    val id: Int,
    val employeeId: Int,
    val startTime: String,
    val endTime: String?,
    val workLocation: WorkLocation,
    val longitude: Double,
    val latitude: Double,
    val imagePath: String? = null,
    val taskLink: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
) {

}