package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.requests.AttendanceApiRequestDto
import okhttp3.Response

interface AttendanceApi {
    suspend fun getAttendanceData(): Response
    suspend fun pushAttendanceChanges(request: AttendanceApiRequestDto): Response

}