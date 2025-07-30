package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.network.PUT
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class MeetingApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com",
) : MeetingApi {

    override suspend fun getMeetings(): Response {
        val request = GET("$baseUrl/meetings")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun createMeeting(meeting: MeetingDto): Response {
        val jsonBody = Json.encodeToString(meeting)
            .toRequestBody("application/json".toMediaType())
        val request = POST("$baseUrl/meetings", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun updateMeeting(id: Int, meeting: MeetingDto): Response {
        val jsonBody = Json.encodeToString(meeting)
            .toRequestBody("application/json".toMediaType())
        val request = PUT("$baseUrl/meetings/$id", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

}
