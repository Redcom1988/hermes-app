package dev.redcom1988.hermes.data.remote.api.impl

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.AttendanceApi
import dev.redcom1988.hermes.data.remote.model.requests.AttendanceApiRequestDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class AttendanceApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com",
) : AttendanceApi {

    // Fetches all attendances and tasks from the server
    override suspend fun getAttendanceData(): Response {
        val request = GET("$baseUrl/attendances")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun pushAttendanceChanges(request: AttendanceApiRequestDto): Response {
        val json = Json.encodeToString(AttendanceApiRequestDto.serializer(), request)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val requestObj = POST("$baseUrl/attendances/sync", body = requestBody)
        return networkHelper.client.newCall(requestObj).await()
    }

}