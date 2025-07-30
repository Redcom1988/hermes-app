package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.network.PUT
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.model.TaskDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class TaskApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com/"
) : TaskApi {

    override suspend fun getTasks(): Response {
        val request = GET("$baseUrl/tasks")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun createTask(task: TaskDto): Response {
        val jsonBody = Json.encodeToString(task)
            .toRequestBody("application/json".toMediaType())
        val request = POST("$baseUrl/tasks", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun updateTask(id: Int, task: TaskDto): Response {
        val jsonBody = Json.encodeToString(task)
            .toRequestBody("application/json".toMediaType())
        val request = PUT("$baseUrl/tasks/$id", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

}