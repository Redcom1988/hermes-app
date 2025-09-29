package dev.redcom1988.hermes.data.remote.api.impl

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.network.PUT
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.TaskApi
import dev.redcom1988.hermes.data.remote.model.TaskDto
import dev.redcom1988.hermes.data.remote.model.requests.MeetingApiRequestDto
import dev.redcom1988.hermes.data.remote.model.requests.TaskApiRequestDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class TaskApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com/"
) : TaskApi {

    override suspend fun getTaskData(): Response {
        val request = GET("$baseUrl/tasks")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun pushTaskChanges(request: TaskApiRequestDto): Response {
        val json = Json.encodeToString(TaskApiRequestDto.serializer(), request)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val requestObj = POST("$baseUrl/tasks/sync", body = requestBody)
        return networkHelper.client.newCall(requestObj).await()
    }

}