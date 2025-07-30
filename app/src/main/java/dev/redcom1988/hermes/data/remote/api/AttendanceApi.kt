package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import okhttp3.Response

interface AttendanceApi {
    suspend fun getAttendances(): Response
    suspend fun createAttendance(attendance: AttendanceDto): Response
    suspend fun updateAttendance(id: Int, attendance: AttendanceDto): Response
}