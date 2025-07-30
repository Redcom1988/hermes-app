package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.network.PUT
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class AttendanceApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com",
) : AttendanceApi {

    override suspend fun getAttendances(): Response {
        val request = GET("$baseUrl/attendances")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun createAttendance(attendance: AttendanceDto): Response {
        val jsonBody = Json.encodeToString(attendance)
            .toRequestBody("application/json".toMediaType())
        val request = POST("$baseUrl/attendances", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun updateAttendance(id: Int, attendance: AttendanceDto): Response {
        val jsonBody = Json.encodeToString(attendance)
            .toRequestBody("application/json".toMediaType())
        val request = PUT("$baseUrl/attendances/$id", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

}